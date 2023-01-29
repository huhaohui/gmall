package com.atguigu.gmall.payment.interceptors;

import com.atguigu.gmall.payment.pojo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
//@Data
public class LoginInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userIdString = request.getHeader("userId");
        Long userId = null;
        if (StringUtils.isNotBlank(userIdString)){
            userId = Long.valueOf(userIdString);
        }
        String username = request.getHeader("username");

        // 已经获取了登录信息 userId userKey
        UserInfo userInfo = new UserInfo(userId, null, username);
        THREAD_LOCAL.set(userInfo);

        return true;
    }

    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 必须手动释放局部变量，因为我们这里使用的是tomcat线程池，否则导致内存泄漏
        THREAD_LOCAL.remove();
    }
}
