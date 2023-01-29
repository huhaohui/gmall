package com.atguigu.gmall.oms.mapper;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2022-10-26 16:43:16
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {

    int updateStatus(@Param("orderToken") String orderToken, @Param("expect") Integer expect, @Param("target") Integer target);
}
