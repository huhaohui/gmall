package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2023/1/7 20:40
 * @Email: 1796235969@qq.com
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
