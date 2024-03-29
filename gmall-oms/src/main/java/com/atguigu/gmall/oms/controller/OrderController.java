package com.atguigu.gmall.oms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.service.OrderService;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单
 *
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2022-10-26 16:43:16
 */
@Api(tags = "订单 管理")
@RestController
@RequestMapping("oms/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("token/{orderToken}")
    public ResponseVo<OrderEntity> queryOrderByToken(@PathVariable("orderToken")String orderToken){
        OrderEntity orderEntity = this.orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderToken));
        return ResponseVo.ok(orderEntity);
    }

    @PostMapping("save/{userId}")
    public ResponseVo saveOrder(@RequestBody OrderSubmitVo submitVo, @PathVariable("userId")Long userId){
        this.orderService.saveOrder(submitVo, userId);
        return ResponseVo.ok();
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryOrderByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = orderService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<OrderEntity> queryOrderById(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return ResponseVo.ok(order);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody OrderEntity order){
		orderService.save(order);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		orderService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
