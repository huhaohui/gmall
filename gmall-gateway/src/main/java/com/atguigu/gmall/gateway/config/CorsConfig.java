package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2022/10/27 07:17
 * @Email: moumouguan@gmail.com
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        // 初始化 一个 cors 配置类对象
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许跨域访问的域名. * 代表所有域名. 不推荐 1. 存在安全问题 2. 不能携带 cookie
        configuration.addAllowedOrigin("http://manager.gmall.com");
        configuration.addAllowedOrigin("http://localhost:1000");
        configuration.addAllowedOrigin("http://www.gmall.com");
        configuration.addAllowedOrigin("http://localhost:18087");
        configuration.addAllowedOrigin("http://192.168.0.118");
        configuration.addAllowedOrigin("http://127.0.0.1:1000");
        // 允许那些请求方式跨域访问 * 允许所有
        configuration.addAllowedMethod("*");
        // 允许携带的头信息 * 允许所有
        configuration.addAllowedHeader("*");
        // 允许 cookie 跨域访问, 需要满足两点 1. AllowedOrigin 不能写 * 2. 此处需要设置为 true
        configuration.setAllowCredentials(true);

        // 初始化 cors 配置源
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        // 注册 cors 配置. /** 针对所有路径 做 cors 配置验证.
        configurationSource.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(configurationSource);
    }

}
