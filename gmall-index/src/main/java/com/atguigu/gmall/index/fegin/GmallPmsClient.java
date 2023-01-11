package com.atguigu.gmall.index.fegin;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/11 17:49
 * @Email: 1656311081@qq.com
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
