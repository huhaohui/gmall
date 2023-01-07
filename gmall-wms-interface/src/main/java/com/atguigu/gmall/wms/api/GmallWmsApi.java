package com.atguigu.gmall.wms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2023/1/7 21:33
 * @Email: 1796235969@qq.com
 */
public interface GmallWmsApi {

    @GetMapping("wms/waresku/sku/{skuId}")
    @ApiOperation("获取某个sku的库存信息")
    ResponseVo<List<WareSkuEntity>> queryWareSkuBySkuId(@PathVariable("skuId") Long skuId);

}
