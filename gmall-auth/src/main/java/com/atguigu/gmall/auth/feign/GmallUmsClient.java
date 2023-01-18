package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/17 19:32
 * @Email: 1656311081@qq.com
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {
}
