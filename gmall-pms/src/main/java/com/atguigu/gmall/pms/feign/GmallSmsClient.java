package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2022/11/2 9:58
 * @Email: 1796235969@qq.com
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
