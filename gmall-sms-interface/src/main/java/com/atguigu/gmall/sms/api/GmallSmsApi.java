package com.atguigu.gmall.sms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2022/11/2 10:23
 * @Email: 1796235969@qq.com
 */
public interface GmallSmsApi {

    @PostMapping("sms/skubounds/sales/save")
    ResponseVo saveSales(@RequestBody SkuSaleVo saleVo);

    @GetMapping("sms/skubounds/sku/{skuId}")
    @ApiOperation("根据skuId查询营销信息")
    ResponseVo<List<ItemSaleVo>> querySalesBySkuId(@PathVariable("skuId")Long skuId);

}
