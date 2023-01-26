package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping("cart.html")
    public String queryCarts(Model model){

        List<Cart> carts = this.cartService.queryCarts();
        model.addAttribute("carts", carts);

        return "cart";
    }

    @PostMapping("updateNum")
    @ResponseBody
    public ResponseVo updateNum(@RequestBody Cart cart){
        this.cartService.updateNum(cart);
        return ResponseVo.ok();
    }

    @PostMapping("updateStatus")
    @ResponseBody
    public ResponseVo updateStatus(@RequestBody Cart cart){
        this.cartService.updateStatus(cart);
        return ResponseVo.ok();
    }

    @PostMapping("deleteCart")
    @ResponseBody
    public ResponseVo deleteCart(@RequestParam("skuId")Long skuId){
        this.cartService.deleteCart(skuId);
        return ResponseVo.ok();
    }

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
