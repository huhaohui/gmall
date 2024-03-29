package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/28 17:05
 * @Email: 1656311081@qq.com
 */
public interface GmallCartApi {

    @GetMapping("checked/carts/{userId}")
    @ResponseBody
    ResponseVo<List<Cart>> queryCheckedCartsByUserId(@PathVariable("userId")Long userId);

}
