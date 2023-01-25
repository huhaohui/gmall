package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/24 15:53
 * @Email: 1656311081@qq.com
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String saveCart(Cart cart){
        this.cartService.saveCart(cart);
        return "redirect:http://cart.gmall.com/addCart.html?skuId=" + cart.getSkuId() + "&count=" + cart.getCount();
    }

    @GetMapping("addCart.html")
    public String queryCart(Cart cart, Model model){
        BigDecimal count = cart.getCount();
        cart = this.cartService.queryCartBySkuId(cart.getSkuId());
        cart.setCount(count);
        model.addAttribute("cart", cart);
        return "addCart";
    }


}
