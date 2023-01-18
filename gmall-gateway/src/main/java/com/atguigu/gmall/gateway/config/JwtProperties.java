package com.atguigu.gmall.gateway.config;

import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;


/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/17 19:13
 * @Email: 1656311081@qq.com
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String pubKeyPath;
    private String cookieName;
    private String token;

    private PublicKey publicKey;

    @PostConstruct
    public void init(){
        try {
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
