package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.fegin.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedissonClient redissonClient;

    private static final String KEY_PREFIX = "index:cates:";
    private static final String LOCK_PREFIX = "index:cates:lock:";

    public List<CategoryEntity> queryLvllCategories() {
        ResponseVo<List<CategoryEntity>> categoryResponseVo = this.pmsClient.queryCategoriesByPid(0l);
        return categoryResponseVo.getData();
    }

    @GmallCache(prefix = KEY_PREFIX, timeout = 129600, random = 14400, lock = LOCK_PREFIX)
    public List<CategoryEntity> queryLvl23CategoriesByPid(Long pid) {
            ResponseVo<List<CategoryEntity>> categoryResponseVo = this.pmsClient.queryLevel23CategoriesByPid(pid);
            return categoryResponseVo.getData();
    }

    public List<CategoryEntity> queryLvl23CategoriesByPid2(Long pid) {
        // 1.先查询缓存，如果缓存命中则返回
        String json = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseArray(json,CategoryEntity.class);
        }

        // 为了防止缓存击穿，添加分布式锁
        RLock fairLock = this.redissonClient.getFairLock(LOCK_PREFIX + pid);
        fairLock.lock();

        try {
            // 当前请求获取锁的过程中，可能有其他请求已经把数据放入缓存，此时，可以再次查询缓存，如果命中则直接返回
            String json2 = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
            if (StringUtils.isNotBlank(json2)) {
                return JSON.parseArray(json2, CategoryEntity.class);
            }
            // 2.走远程调用，查询数据库，并放入缓存
            ResponseVo<List<CategoryEntity>> categoryResponseVo = this.pmsClient.queryLevel23CategoriesByPid(pid);
            List<CategoryEntity> categoryEntities = categoryResponseVo.getData();

            // 为了解决缓存穿透，数据即使为null也缓存，缓存时间不超过5min
            if (CollectionUtils.isEmpty(categoryEntities)){
                this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities),
                        5, TimeUnit.MINUTES);
            } else {
                // 为了解决缓存雪崩，给缓存时间添加随机值。
                this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntities),
                        90 + new Random().nextInt(10), TimeUnit.DAYS);
            }


            return categoryEntities;
        } finally {
            fairLock.unlock();
        }
    }
}
