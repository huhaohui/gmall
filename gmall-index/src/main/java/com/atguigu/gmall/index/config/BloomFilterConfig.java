package com.atguigu.gmall.index.config;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.fegin.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/14 17:06
 * @Email: 1656311081@qq.com
 */
@Configuration
public class BloomFilterConfig {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GmallPmsClient pmsClient;

    private static final String KEY_PREFIX = "index:cates:";

    @Bean
    public RBloomFilter bloomFilter(){

        RBloomFilter<String> bloomFilter = this.redissonClient.getBloomFilter("index:bf");
        bloomFilter.tryInit(2000, 0.03);

        // 向布隆过滤器中初始化数据：分类 广告
        ResponseVo<List<CategoryEntity>> responseVo = this.pmsClient.queryCategoriesByPid(0L);
        List<CategoryEntity> categoryEntities = responseVo.getData();
        if (!CollectionUtils.isEmpty(categoryEntities)){
            categoryEntities.forEach(categoryEntity -> {
                bloomFilter.add(KEY_PREFIX + categoryEntity.getId());
            });
        }

        return bloomFilter;
    }

}
