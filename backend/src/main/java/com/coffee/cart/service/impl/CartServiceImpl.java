package com.coffee.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coffee.cart.dto.AddCartItemDTO;
import com.coffee.cart.dto.UpdateCartItemDTO;
import com.coffee.cart.entity.Cart;
import com.coffee.cart.entity.CartItem;
import com.coffee.cart.mapper.CartItemMapper;
import com.coffee.cart.mapper.CartMapper;
import com.coffee.cart.service.CartService;
import com.coffee.cart.vo.AddCartItemVO;
import com.coffee.cart.vo.UpdateCartItemVO;
import com.coffee.cart.vo.CartItemVO;
import com.coffee.cart.vo.CartVO;
import com.coffee.catalog.entity.CoffeeSku;
import com.coffee.catalog.mapper.CoffeeSkuMapper;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车业务实现
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CoffeeSkuMapper skuMapper;

    // 单品上限
    private static final int MAX_QUANTITY_PER_ITEM = 99;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddCartItemVO addItem(Long userId, AddCartItemDTO dto) {
        // 校验 SKU 是否存在且库存充足
        CoffeeSku sku = skuMapper.selectById(dto.getSkuId());
        if (sku == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品规格不存在");
        }
        if (sku.getStock() < dto.getQuantity()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足");
        }

        // 获取或创建购物车
        Cart cart = getOrCreateCart(userId);

        // 检查购物车中是否已有该 SKU
        CartItem existingItem = cartItemMapper.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getCartId, cart.getId())
                        .eq(CartItem::getSkuId, dto.getSkuId())
        );

        if (existingItem != null) {
            // 合并数量
            int newQuantity = existingItem.getQuantity() + dto.getQuantity();
            if (newQuantity > MAX_QUANTITY_PER_ITEM) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "单个商品限购 " + MAX_QUANTITY_PER_ITEM + " 件");
            }
            if (sku.getStock() < newQuantity) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setChecked(1); // 重新加购时默认勾选
            cartItemMapper.updateById(existingItem);
            return toAddVO(existingItem);
        } else {
            // 新增
            if (dto.getQuantity() > MAX_QUANTITY_PER_ITEM) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "单个商品限购 " + MAX_QUANTITY_PER_ITEM + " 件");
            }
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getId());
            newItem.setSkuId(dto.getSkuId());
            newItem.setQuantity(dto.getQuantity());
            newItem.setChecked(1);
            cartItemMapper.insert(newItem);
            return toAddVO(newItem);
        }
    }

    private AddCartItemVO toAddVO(CartItem item) {
        AddCartItemVO vo = new AddCartItemVO();
        vo.setCartItemId(item.getId());
        vo.setSkuId(item.getSkuId());
        vo.setQuantity(item.getQuantity());
        return vo;
    }

    @Override
    public CartVO getCart(Long userId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        CartVO vo = new CartVO();
        if (cart == null) {
            return vo; // 返回空列表
        }

        List<CartItemVO> items = cartItemMapper.selectCartItemVOs(cart.getId());
        vo.setItems(items);

        // 计算勾选商品的总金额
        BigDecimal totalAmount = items.stream()
                .filter(item -> item.getChecked() != null && item.getChecked() == 1)
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalAmount(totalAmount);

        return vo;
    }

    @Override
    public UpdateCartItemVO updateItem(Long userId, Long cartItemId, UpdateCartItemDTO dto) {
        CartItem item = getValidCartItem(userId, cartItemId);
        
        if (dto.getQuantity() != null) {
            // 校验库存
            CoffeeSku sku = skuMapper.selectById(item.getSkuId());
            if (sku.getStock() < dto.getQuantity()) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足");
            }
            item.setQuantity(dto.getQuantity());
        }
        if (dto.getChecked() != null) {
            item.setChecked(dto.getChecked());
        }
        
        cartItemMapper.updateById(item);

        UpdateCartItemVO vo = new UpdateCartItemVO();
        vo.setId(item.getId());
        vo.setQuantity(item.getQuantity());
        vo.setChecked(item.getChecked());
        return vo;
    }

    @Override
    public void removeItem(Long userId, Long cartItemId) {
        CartItem item = getValidCartItem(userId, cartItemId);
        cartItemMapper.deleteById(item.getId());
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        if (cart != null) {
            cartItemMapper.delete(new LambdaQueryWrapper<CartItem>().eq(CartItem::getCartId, cart.getId()));
        }
    }

    // ===== 私有辅助方法 =====

    // 获取或创建用户购物车
    private Cart getOrCreateCart(Long userId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cartMapper.insert(cart);
        }
        return cart;
    }

    // 校验购物车项归属，防止越权
    private CartItem getValidCartItem(Long userId, Long cartItemId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        if (cart == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "购物车不存在");
        }
        CartItem item = cartItemMapper.selectOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getId, cartItemId)
                        .eq(CartItem::getCartId, cart.getId())
        );
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "购物车商品不存在");
        }
        return item;
    }
}