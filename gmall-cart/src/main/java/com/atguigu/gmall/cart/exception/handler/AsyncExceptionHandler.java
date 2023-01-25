package com.atguigu.gmall.cart.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/25 18:59
 * @Email: 1656311081@qq.com
 */
@Slf4j
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        // 记录日志 或者 记录到数据库
        log.error("异步任务执行失败。失败信息：{}，方法：{}，参数列表：{}", throwable.getMessage(), method.getName(), Arrays.asList(objects));
    }
}
