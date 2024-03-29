package com.atguigu.gmall.auth.controller;

import com.atguigu.gmall.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/17 19:31
 * @Email: 1656311081@qq.com
 */
@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public String login(
            @RequestParam("returnUrl")String returnUrl,
            @RequestParam("loginName")String loginName,
            @RequestParam("password")String password,
            HttpServletRequest request, HttpServletResponse response
    ){
        this.authService.login(loginName, password, request, response);
        // 登录成功，重定向回到之前路径
        return "redirect:" + returnUrl;
    }

    @GetMapping("toLogin.html")
    public String toLogin(@RequestParam(value = "returnUrl", defaultValue = "http://gmall.com")String returnUrl, Model model){
        model.addAttribute("returnUrl", returnUrl);
        return "login";
    }

}
