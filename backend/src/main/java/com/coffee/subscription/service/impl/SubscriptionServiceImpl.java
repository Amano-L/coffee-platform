package com.coffee.subscription.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coffee.catalog.entity.CoffeeProduct;
import com.coffee.catalog.entity.CoffeeSku;
import com.coffee.catalog.mapper.CoffeeProductMapper;
import com.coffee.catalog.mapper.CoffeeSkuMapper;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.order.entity.CoffeeOrder;
import com.coffee.order.entity.CoffeeOrderItem;
import com.coffee.order.mapper.CoffeeOrderItemMapper;
import com.coffee.order.mapper.CoffeeOrderMapper;
import com.coffee.subscription.dto.CreateSubscriptionDTO;
import com.coffee.subscription.entity.SubscriptionOrder;
import com.coffee.subscription.entity.SubscriptionPlan;
import com.coffee.subscription.entity.UserSubscription;
import com.coffee.subscription.mapper.SubscriptionOrderMapper;
import com.coffee.subscription.mapper.SubscriptionPlanMapper;
import com.coffee.subscription.mapper.UserSubscriptionMapper;
import com.coffee.subscription.service.SubscriptionService;
import com.coffee.subscription.vo.SubscriptionPlanVO;
import com.coffee.subscription.vo.UserSubscriptionVO;
import com.coffee.user.entity.UserAddress;
import com.coffee.user.mapper.UserAddressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订阅业务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPlanMapper planMapper;
    private final UserSubscriptionMapper userSubscriptionMapper;
    private final SubscriptionOrderMapper subscriptionOrderMapper;
    private final CoffeeSkuMapper skuMapper;
    private final CoffeeProductMapper productMapper;
    private final CoffeeOrderMapper orderMapper;
    private final CoffeeOrderItemMapper orderItemMapper;
    private final UserAddressMapper addressMapper;

    // 注入自身代理，解决 @Transactional 自调用失效问题
    @Autowired
    @Lazy
    private SubscriptionServiceImpl self;

    @Override
    public List<SubscriptionPlanVO> getPlans() {
        List<SubscriptionPlan> plans = planMapper.selectList(null);
        return plans.stream().map(p -> {
            SubscriptionPlanVO vo = new SubscriptionPlanVO();
            vo.setId(p.getId());
            vo.setName(p.getName());
            vo.setPeriod(p.getPeriod());
            vo.setPrice(p.getPrice());
            vo.setDescription(p.getDescription());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public UserSubscriptionVO createSubscription(Long userId, CreateSubscriptionDTO dto) {
        SubscriptionPlan plan = planMapper.selectById(dto.getPlanId());
        if (plan == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订阅计划不存在");
        }

        // 检查是否已订阅
        Long count = userSubscriptionMapper.selectCount(
                new LambdaQueryWrapper<UserSubscription>()
                        .eq(UserSubscription::getUserId, userId)
                        .eq(UserSubscription::getPlanId, plan.getId())
                        .eq(UserSubscription::getStatus, "ACTIVE")
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "您已订阅此计划");
        }

        UserSubscription sub = new UserSubscription();
        sub.setUserId(userId);
        sub.setPlanId(plan.getId());
        sub.setStatus("ACTIVE");

        // 初始配送日期设为明天
        sub.setNextDeliveryDate(LocalDate.now().plusDays(1));

        // 设置创建时间
        sub.setCreateTime(LocalDateTime.now());

        userSubscriptionMapper.insert(sub);

        return toVO(sub, plan);
    }

    @Override
    public List<UserSubscriptionVO> getMySubscriptions(Long userId) {
        List<UserSubscription> subs = userSubscriptionMapper.selectList(
                new LambdaQueryWrapper<UserSubscription>().eq(UserSubscription::getUserId, userId)
        );
        return subs.stream().map(sub -> {
            SubscriptionPlan plan = planMapper.selectById(sub.getPlanId());
            return toVO(sub, plan);
        }).collect(Collectors.toList());
    }

    @Override
    public void pauseSubscription(Long userId, Long id) {
        UserSubscription sub = getValidSubscription(userId, id);
        if (!"ACTIVE".equals(sub.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前状态无法暂停");
        }
        sub.setStatus("PAUSED");
        userSubscriptionMapper.updateById(sub);
    }

    @Override
    public void skipSubscription(Long userId, Long id) {
        UserSubscription sub = getValidSubscription(userId, id);
        if (!"ACTIVE".equals(sub.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前状态无法跳过");
        }
        SubscriptionPlan plan = planMapper.selectById(sub.getPlanId());
        // 顺延一次周期
        sub.setNextDeliveryDate(calculateNextDate(sub.getNextDeliveryDate(), plan.getPeriod()));
        userSubscriptionMapper.updateById(sub);
    }

    @Override
    public void cancelSubscription(Long userId, Long id) {
        UserSubscription sub = getValidSubscription(userId, id);
        if ("CANCELLED".equals(sub.getStatus())) {
            return; // 幂等
        }
        sub.setStatus("CANCELLED");
        userSubscriptionMapper.updateById(sub);
    }

    // ================= 定时任务核心逻辑 =================

    @Override
    public void processExpiredSubscriptions() {
        // 找出今天需要配送的活跃订阅
        List<UserSubscription> list = userSubscriptionMapper.selectList(
                new LambdaQueryWrapper<UserSubscription>()
                        .eq(UserSubscription::getStatus, "ACTIVE")
                        .le(UserSubscription::getNextDeliveryDate, LocalDate.now())
        );

        for (UserSubscription sub : list) {
            try {
                // 通过代理调用，保证事务生效
                self.generateOrderForSubscription(sub);
            } catch (Exception e) {
                log.error("生成订阅订单失败, subscriptionId: {}, 原因: {}", sub.getId(), e.getMessage());
                // 库存不足或其他错误静默跳过，继续下一个
            }
        }
    }

    // 为单个订阅生成订单 (需要开启新事务)
    @Transactional(rollbackFor = Exception.class)
    public void generateOrderForSubscription(UserSubscription sub) {
        SubscriptionPlan plan = planMapper.selectById(sub.getPlanId());
        if (plan == null) throw new BusinessException(ResultCode.NOT_FOUND, "订阅计划已被删除");

        CoffeeSku sku = skuMapper.selectById(plan.getSkuId());
        if (sku == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "SKU不存在");
        }

        // 1. 扣减库存 (乐观锁)
        int affected = skuMapper.deductStock(sku.getId(), 1, sku.getVersion());
        if (affected == 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足");
        }

        // 2. 获取默认地址
        UserAddress address = addressMapper.selectOne(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, sub.getUserId())
                        .eq(UserAddress::getIsDefault, 1)
                        .last("LIMIT 1")
        );
        if (address == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户无默认地址");
        }

        // 3. 生成订单
        String orderNo = "SUB" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(1000, 9999);

        CoffeeOrder order = new CoffeeOrder();
        order.setOrderNo(orderNo);
        order.setUserId(sub.getUserId());
        order.setTotalAmount(plan.getPrice());
        order.setPayAmount(plan.getPrice());
        order.setStatus("待支付");
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        orderMapper.insert(order);

        // 4. 生成订单项
        CoffeeProduct product = productMapper.selectById(sku.getProductId());
        CoffeeOrderItem item = new CoffeeOrderItem();
        item.setOrderId(order.getId());
        item.setSkuId(sku.getId());
        item.setProductName(product != null ? product.getName() : "咖啡商品");
        item.setPrice(plan.getPrice());
        item.setQuantity(1);
        orderItemMapper.insert(item);

        // 5. 记录订阅订单
        SubscriptionOrder subOrder = new SubscriptionOrder();
        subOrder.setUserSubscriptionId(sub.getId());
        subOrder.setOrderId(order.getId());
        subOrder.setOrderNo(orderNo);
        subOrder.setDeliveryDate(LocalDate.now());
        subOrder.setStatus("待支付");
        subscriptionOrderMapper.insert(subOrder);

        // 6. 更新订阅的下次配送时间
        sub.setNextDeliveryDate(calculateNextDate(sub.getNextDeliveryDate(), plan.getPeriod()));
        userSubscriptionMapper.updateById(sub);

        log.info("成功为订阅 {} 生成订单 {}", sub.getId(), orderNo);
    }

    // ================= 辅助方法 =================

    // 根据周期计算下次配送日期
    private LocalDate calculateNextDate(LocalDate current, String period) {
        if ("WEEKLY".equals(period)) {
            return current.plusWeeks(1);
        } else if ("BIWEEKLY".equals(period)) {
            return current.plusWeeks(2);
        } else {
            return current.plusMonths(1); // 默认 MONTHLY
        }
    }

    // 校验订阅归属，防止越权
    private UserSubscription getValidSubscription(Long userId, Long id) {
        UserSubscription sub = userSubscriptionMapper.selectOne(
                new LambdaQueryWrapper<UserSubscription>()
                        .eq(UserSubscription::getId, id)
                        .eq(UserSubscription::getUserId, userId)
        );
        if (sub == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订阅不存在");
        }
        return sub;
    }

    // Entity 转 VO
    private UserSubscriptionVO toVO(UserSubscription sub, SubscriptionPlan plan) {
        UserSubscriptionVO vo = new UserSubscriptionVO();
        vo.setId(sub.getId());
        vo.setStatus(sub.getStatus());
        vo.setNextDeliveryDate(sub.getNextDeliveryDate());
        vo.setCreateTime(sub.getCreateTime());
        if (plan != null) {
            vo.setPlanName(plan.getName());
            vo.setPeriod(plan.getPeriod());
        }
        return vo;
    }
}