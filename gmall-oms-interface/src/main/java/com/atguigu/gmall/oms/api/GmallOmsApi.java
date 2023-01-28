package com.atguigu.gmall.oms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/28 20:21
 * @Email: 1656311081@qq.com
 */
public interface GmallOmsApi {

    @PostMapping("oms/order/save/{userId}")
    ResponseVo saveOrder(@RequestBody OrderSubmitVo submitVo, @PathVariable("userId")Long userId);

}
