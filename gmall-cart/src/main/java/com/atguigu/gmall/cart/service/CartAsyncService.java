package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.cart.pojo.Cart;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/25 17:21
 * @Email: 1656311081@qq.com
 */
@Service
public class CartAsyncService {

    @Autowired
    private CartMapper cartMapper;

    @Async
    public void updateCart(String userId, String skuId, Cart cart){
        this.cartMapper.update(cart, new UpdateWrapper<Cart>().eq("user_id", userId).eq("sku_id", skuId));
    }

    @Async
    public void insertCart(Cart cart){
        this.cartMapper.insert(cart);
    }

    @Async
    public void deleteByUserId(String userId) {
        this.cartMapper.delete(new UpdateWrapper<Cart>().eq("user_id", userId));
    }

    @Async
    public void deleteByUserIdAndSkuId(String userId, Long skuId) {
        this.cartMapper.delete(new UpdateWrapper<Cart>().eq("user_id", userId).eq("sku_id", skuId));
    }

}
