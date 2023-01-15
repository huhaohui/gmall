package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/14 20:01
 * @Email: 1656311081@qq.com
 */
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}.html")
    @ResponseBody
    public ResponseVo<ItemVo> loadData(@PathVariable("skuId")Long skuId){
        ItemVo itemVo = this.itemService.loadData(skuId);
        return ResponseVo.ok(itemVo);
    }

}
