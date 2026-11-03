package com.coffee.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.coffee.cart.entity.Cart;
import com.coffee.cart.entity.CartItem;
import com.coffee.cart.mapper.CartItemMapper;
import com.coffee.cart.mapper.CartMapper;
import com.coffee.catalog.entity.CoffeeSku;
import com.coffee.catalog.mapper.CoffeeSkuMapper;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.common.util.RabbitMqUtil;
import com.coffee.order.dto.CreateOrderDTO;
import com.coffee.order.entity.CoffeeOrder;
import com.coffee.order.entity.CoffeeOrderItem;
import com.coffee.order.mapper.CoffeeOrderItemMapper;
import com.coffee.order.mapper.CoffeeOrderMapper;
import com.coffee.order.service.OrderService;
import com.coffee.order.vo.OrderSubmitVO;
import com.coffee.user.entity.UserAddress;
import com.coffee.user.mapper.UserAddressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订单业务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CoffeeOrderMapper orderMapper;
    private final CoffeeOrderItemMapper orderItemMapper;
    private final CoffeeSkuMapper skuMapper;
    private final UserAddressMapper addressMapper;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final RabbitMqUtil rabbitMqUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitVO createOrder(Long userId, CreateOrderDTO dto) {
        // 1. 校验地址
        UserAddress address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "收货地址不存在");
        }

        // 2. 校验购物车项
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        if (cart == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "购物车为空");
        }

        List<CartItem> cartItems = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getCartId, cart.getId())
                        .in(CartItem::getId, dto.getCartItemIds())
        );
        if (cartItems.isEmpty() || cartItems.size() != dto.getCartItemIds().size()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "部分商品不存在或已被移除");
        }

        // 3. 生成订单号
        String orderNo = "COF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(1000, 9999);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 4. 扣减库存 & 计算金额
        for (CartItem item : cartItems) {
            CoffeeSku sku = skuMapper.selectById(item.getSkuId());
            if (sku == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "商品规格不存在");
            }
            
            // 乐观锁扣减库存
            int affectedRows = skuMapper.deductStock(sku.getId(), item.getQuantity(), sku.getVersion());
            if (affectedRows == 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "商品 [" + sku.getId() + "] 库存不足，请重新选择");
            }

            // 累加金额
            BigDecimal itemTotal = sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 5. 插入订单主表
        CoffeeOrder order = new CoffeeOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount); // 简化：暂不考虑运费和优惠券
        order.setStatus("待支付");
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        orderMapper.insert(order);

        // 6. 插入订单项表（保存商品快照）
        for (CartItem item : cartItems) {
            CoffeeSku sku = skuMapper.selectById(item.getSkuId());
            // 查一下商品名用于快照（这里简化查一下，实际可以批量查）
            com.coffee.catalog.entity.CoffeeProduct product = null; // 为了代码完整，你需要注入 CoffeeProductMapper 去查
            
            CoffeeOrderItem orderItem = new CoffeeOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setProductName("咖啡商品"); // 此处简化，可以替换为 product.getName()
            orderItem.setPrice(sku.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemMapper.insert(orderItem);
        }

        // 7. 清理购物车中已下单的项
        cartItemMapper.deleteBatchIds(dto.getCartItemIds());

        // 8. 发送延迟消息（30分钟超时取消）
        rabbitMqUtil.sendOrderTimeoutMessage(orderNo);

        // 9. 返回
        OrderSubmitVO vo = new OrderSubmitVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(totalAmount);
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        // 1. 查订单
        CoffeeOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<CoffeeOrder>().eq(CoffeeOrder::getOrderNo, orderNo)
        );

        // 2. 幂等判断：如果订单不存在，或者状态已经不是待支付，直接返回
        if (order == null || !"待支付".equals(order.getStatus())) {
            log.info("订单 {} 不存在或状态不是待支付，无需取消", orderNo);
            return;
        }

        // 3. 更新订单状态为已取消
        // 使用 UpdateWrapper 更新，加上 status = '待支付' 条件，防止并发下把已支付的订单改成取消
        int updated = orderMapper.update(null,
                new LambdaUpdateWrapper<CoffeeOrder>()
                        .eq(CoffeeOrder::getId, order.getId())
                        .eq(CoffeeOrder::getStatus, "待支付")
                        .set(CoffeeOrder::getStatus, "已取消")
        );

        if (updated == 0) {
            log.info("订单 {} 状态已被修改，放弃取消", orderNo);
            return;
        }

        // 4. 回滚库存
        List<CoffeeOrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<CoffeeOrderItem>().eq(CoffeeOrderItem::getOrderId, order.getId())
        );
        for (CoffeeOrderItem item : items) {
            skuMapper.addStock(item.getSkuId(), item.getQuantity());
        }

        log.info("订单 {} 超时未支付，已成功取消并释放库存", orderNo);
    }
}