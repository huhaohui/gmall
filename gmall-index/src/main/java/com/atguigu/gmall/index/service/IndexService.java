package com.atguigu.gmall.index.service;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.fegin.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/11 17:48
 * @Email: 1656311081@qq.com
 */
@Service
public class IndexService {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public List<CategoryEntity> queryLvllCategories() {
        ResponseVo<List<CategoryEntity>> categoryResponseVo = this.pmsClient.queryCategoriesByPid(0l);
        return categoryResponseVo.getData();
    }

    public List<CategoryEntity> queryLvl23CategoriesByPid(Long pid) {
        ResponseVo<List<CategoryEntity>> categoryResponseVo = this.pmsClient.queryLevel23CategoriesByPid(pid);
        List<CategoryEntity> categoryEntities = categoryResponseVo.getData();
        return categoryEntities;
    }
}
