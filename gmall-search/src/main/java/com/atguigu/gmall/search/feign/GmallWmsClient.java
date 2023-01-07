package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2023/1/7 21:36
 * @Email: 1796235969@qq.com
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {
}
