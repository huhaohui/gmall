package com.atguigu.gmall.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@MapperScan("com.atguigu.gmall.cart.mapper")
public class GmallCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallCartApplication.class, args);
	}

}
