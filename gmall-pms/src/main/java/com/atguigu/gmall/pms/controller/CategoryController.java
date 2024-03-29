package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;
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
 * 商品三级分类
 *
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2022-10-26 16:07:02
 */
@Api(tags = "商品三级分类 管理")
@RestController
@RequestMapping("pms/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("lvl123/{cid3}")
    @ApiOperation("根据三级分类id查询一二三级分类")
    public ResponseVo<List<CategoryEntity>> queryLvl123CategoriesByCid3(@PathVariable("cid3")Long cid3){
        List<CategoryEntity> categoryEntities = this.categoryService.queryLvl123CategoriesByCid3(cid3);
        return ResponseVo.ok(categoryEntities);
    }

    @GetMapping("level23/{pid}")
    @ApiOperation("查询二三级分类")
    public ResponseVo<List<CategoryEntity>> queryLevel23CategoriesByPid(@PathVariable("pid")Long pid){
        List<CategoryEntity> categoryEntities = this.categoryService.queryLevel23CategoriesByPid(pid);
        return ResponseVo.ok(categoryEntities);
    }

    /**
     * http://api.gmall.com/pms/category/parent/-1
     * 　　　　　　　　　　　　/pms/category/parent/{parentId}
     * <p>
     * 　　　　根据父分类 id 查询子分类
     */
    @GetMapping("parent/{parentId}")
    @ApiOperation("根据父分类 id 查询子分类")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByPid(@PathVariable("parentId") Long pid) {
        List<CategoryEntity> categoryEntities = categoryService.queryCategoriesByPid(pid);

        return ResponseVo.ok(categoryEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryCategoryByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = categoryService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id){
		CategoryEntity category = categoryService.getById(id);

        return ResponseVo.ok(category);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		categoryService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
