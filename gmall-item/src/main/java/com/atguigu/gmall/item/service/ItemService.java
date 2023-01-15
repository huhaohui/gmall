package com.atguigu.gmall.item.service;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/14 20:01
 * @Email: 1656311081@qq.com
 */
@Service
public class ItemService {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private GmallWmsClient wmsClient;

    public ItemVo loadData(Long skuId) {
        ItemVo itemVo = new ItemVo();
//        1.根据skuId查询sku
        ResponseVo<SkuEntity> skuEntityResponseVo = this.pmsClient.querySkuById(skuId);
        SkuEntity skuEntity = skuEntityResponseVo.getData();
        if (skuEntity==null){
            throw new RuntimeException("您访问的商品不存在！");
        }
        itemVo.setSkuId(skuId);
        itemVo.setTitle(skuEntity.getTitle());
        itemVo.setSubtitle(skuEntity.getSubtitle());
        itemVo.setPrice(skuEntity.getPrice());
        itemVo.setWeight(skuEntity.getWeight());
        itemVo.setDefaultImage(skuEntity.getDefaultImage());
//        2.根据三级分类的id查询一二三级分类
        ResponseVo<List<CategoryEntity>> categoryResponseVo = this.pmsClient.queryLvl123CategoriesByCid3(skuEntity.getCategoryId());
        List<CategoryEntity> categoryEntities = categoryResponseVo.getData();
        itemVo.setCategories(categoryEntities);
//        3.根据品牌id查询品牌
        ResponseVo<BrandEntity> brandEntityResponseVo = this.pmsClient.queryBrandById(skuEntity.getBrandId());
        BrandEntity brandEntity = brandEntityResponseVo.getData();
        if (brandEntity != null) {
            itemVo.setBrandId(brandEntity.getId());
            itemVo.setBrandName(brandEntity.getName());
        }
//        4.根据spuId查询spu
        ResponseVo<SpuEntity> spuEntityResponseVo = this.pmsClient.querySpuById(skuEntity.getSpuId());
        SpuEntity spuEntity = spuEntityResponseVo.getData();
        if (spuEntity!=null){
            itemVo.setSpuId(spuEntity.getId());
            itemVo.setSpuName(spuEntity.getName());
        }
//        5.根据skuId查询sku的图片列表
        ResponseVo<List<SkuImagesEntity>> imagesResponseVo = this.pmsClient.queryImagesBySkuId(skuId);
        List<SkuImagesEntity> imagesEntities = imagesResponseVo.getData();
        itemVo.setImage(imagesEntities);
//        6.根据skuId查询营销信息
        ResponseVo<List<ItemSaleVo>> salesResponseVo = this.smsClient.querySalesBySkuId(skuId);
        List<ItemSaleVo> itemSaleVos = salesResponseVo.getData();
        itemVo.setSales(itemSaleVos);
//        7.根据skuId查询库存
        ResponseVo<List<WareSkuEntity>> wareSkuResponseVo = this.wmsClient.queryWareSkuBySkuId(skuId);
        List<WareSkuEntity> wareSkuEntities = wareSkuResponseVo.getData();
        if (!CollectionUtils.isEmpty(wareSkuEntities)){
            itemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
        }
//        8.根据spuId查询spu下所有sku的销售属性列表
        ResponseVo<List<SaleAttrValueVo>> saleAttrsResponseVo = this.pmsClient.querySaleAttrValuesBySpuId(skuEntity.getSpuId());
        List<SaleAttrValueVo> attrValueVos = saleAttrsResponseVo.getData();
        itemVo.setSaleAttrs(attrValueVos);
//        9.根据skuId查询当前sku的销售属性
        ResponseVo<List<SkuAttrValueEntity>> saleAttrResponseVo = this.pmsClient.querySaleAttrValuesBySkuId(skuId);
        List<SkuAttrValueEntity> skuAttrValueEntities = saleAttrResponseVo.getData();
        if (!CollectionUtils.isEmpty(skuAttrValueEntities)){
            itemVo.setSaleAttr(skuAttrValueEntities.stream().collect(Collectors.toMap(SkuAttrValueEntity::getAttrId, SkuAttrValueEntity::getAttrValue)));
        }
//        10.根据spuId查询spu下所有销售属性组合与skuId的映射关系
        ResponseVo<String> stringResponseVo = this.pmsClient.queryMappingBySpuId(skuEntity.getSpuId());
        String json = stringResponseVo.getData();
        itemVo.setSkuJsons(json);
//        11.根据spuId查询spu的描述信息
        ResponseVo<SpuDescEntity> spuDescEntityResponseVo = this.pmsClient.querySpuDescById(skuEntity.getSpuId());
        SpuDescEntity descEntity = spuDescEntityResponseVo.getData();
        if (descEntity != null) {
            itemVo.setSpuImages(Arrays.asList(StringUtils.split(descEntity.getDecript(), ",")));
        }
//        12.查询规格参数分组及组下的规格参数和值
        ResponseVo<List<ItemGroupVo>> groupResponseVo = this.pmsClient.queryGroupsWithAttrValuesByCidAndSpuIdAndSkuId(skuEntity.getCategoryId(), skuEntity.getSpuId(), skuId);
        itemVo.setGroups(groupResponseVo.getData());
        return itemVo;
    }

}
