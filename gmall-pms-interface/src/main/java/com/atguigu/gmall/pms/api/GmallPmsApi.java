package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2023/1/7 20:36
 * @Email: 1796235969@qq.com
 */
public interface GmallPmsApi {

    @GetMapping("pms/spu/{id}")
    @ApiOperation("详情查询")
    ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    @PostMapping("pms/spu/json")
    @ApiOperation("分页查询")
    ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo);

    @GetMapping("pms/sku/spu/{spuId}")
    @ApiOperation("查询spu的所有sku信息")
    ResponseVo<List<SkuEntity>> querySkusBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/sku/{id}")
    @ApiOperation("详情查询")
    ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);

    @GetMapping("pms/brand/{id}")
    @ApiOperation("详情查询")
    ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    @GetMapping("pms/category/{id}")
    @ApiOperation("详情查询")
    ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    @GetMapping("pms/category/parent/{parentId}")
    @ApiOperation("根据父分类 id 查询子分类")
    ResponseVo<List<CategoryEntity>> queryCategoriesByPid(@PathVariable("parentId") Long pid);

    @GetMapping("pms/category/level23/{pid}")
    @ApiOperation("查询二三级分类")
    ResponseVo<List<CategoryEntity>> queryLevel23CategoriesByPid(@PathVariable("pid")Long pid);

    @GetMapping("pms/category/lvl123/{cid3}")
    @ApiOperation("根据三级分类id查询一二三级分类")
    ResponseVo<List<CategoryEntity>> queryLvl123CategoriesByCid3(@PathVariable("cid3")Long cid3);

    @GetMapping("pms/skuattrvalue/search/attr/value/{cid}")
    @ApiOperation("查询销售类型的检索属性和值")
    ResponseVo<List<SkuAttrValueEntity>> querySearchAttrValueByCidAndSkuId(
            @PathVariable("cid")Long cid,
            @RequestParam("skuId")Long skuId
    );

    @GetMapping("pms/skuattrvalue/spu/{spuId}")
    @ApiOperation("根据spuId查询spu下所有sku的销售属性列表")
    public ResponseVo<List<SaleAttrValueVo>> querySaleAttrValuesBySpuId(@PathVariable("spuId")Long spuId);

    @GetMapping("pms/skuattrvalue/sku/{skuId}")
    @ApiOperation("根据skuId查询当前sku的销售属性")
    ResponseVo<List<SkuAttrValueEntity>> querySaleAttrValuesBySkuId(@PathVariable("skuId")Long skuId);

    @GetMapping("pms/skuattrvalue/mapping/{spuId}")
    @ApiOperation("根据spuId查询spu下所有销售属性组合与skuId的映射关系")
    ResponseVo<String> queryMappingBySpuId(@PathVariable("spuId")Long spuId);

    @GetMapping("pms/spuattrvalue/search/attr/value/{cid}")
    @ApiOperation("查询基本类型的检索属性和值")
    ResponseVo<List<SpuAttrValueEntity>> querySearchAttrValueByCidAndSpuId(
            @PathVariable("cid")Long cid,
            @RequestParam("spuId")Long spuId
    );

    @GetMapping("pms/skuimages/sku/{skuId}")
    @ApiOperation("根据skuId查询sku的图片列表")
    ResponseVo<List<SkuImagesEntity>> queryImagesBySkuId(@PathVariable("skuId")Long skuId);

    @GetMapping("pms/spudesc/{spuId}")
    @ApiOperation("根据spuId查询spu的描述信息")
    ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/attrgroup/with/attr/value/{cid}")
    @ApiOperation("查询规格参数分组及组下的规格参数和值")
    ResponseVo<List<ItemGroupVo>> queryGroupsWithAttrValuesByCidAndSpuIdAndSkuId(
            @PathVariable("cid")Long cid,
            @RequestParam("spuId")Long spuId,
            @RequestParam("skuId")Long skuId
    );

}
