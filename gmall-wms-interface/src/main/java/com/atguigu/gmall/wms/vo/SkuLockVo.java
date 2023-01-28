package com.atguigu.gmall.wms.vo;

import lombok.Data;

@Data
public class SkuLockVo {

    private Long skuId;
    private Integer count;
    private Boolean lock; // 验库存并锁库存是否成功。
    private Long wareSkuId; // 锁定成功的情况下，需要记录库存id
}
