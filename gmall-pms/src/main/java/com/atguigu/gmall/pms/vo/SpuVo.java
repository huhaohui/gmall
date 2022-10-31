package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2022/10/30 10:07
 * @Email: 1796235969@qq.com
 */
@Data
public class SpuVo extends SpuEntity {

    //spu图片列表
    private List<String> spuImages;

    //基本属性
    private List<SpuAttrValueVo> baseAttrs;

    //sku列表
    private List<SkuVo> skus;
}
