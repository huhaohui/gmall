package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 * 
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2022-10-26 16:07:02
 */
@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValueEntity> {

    List<Map<String, Object>> queryMappingBySkuIds(@Param("skuIds")List<Long> skuIds);
}
