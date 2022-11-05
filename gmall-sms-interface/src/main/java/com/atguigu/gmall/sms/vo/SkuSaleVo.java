package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2022/11/2 9:27
 * @Email: 1796235969@qq.com
 */
@Data
public class SkuSaleVo {

    private Long skuId;

    //积分
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    //满减
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

    //打折
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;
}
