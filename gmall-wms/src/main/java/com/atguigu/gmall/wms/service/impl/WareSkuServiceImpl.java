package com.atguigu.gmall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.exception.OrderException;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.mapper.WareSkuMapper;
import com.atguigu.gmall.wms.service.WareSkuService;
import com.atguigu.gmall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSkuEntity> implements WareSkuService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private WareSkuMapper wareSkuMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "stock:lock:";
    private static final String KEY_PREFIX = "stock:info:";

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional
    public List<SkuLockVo> checkLock(List<SkuLockVo> lockVos, String orderToken) {

        // 判空
        if(CollectionUtils.isEmpty(lockVos)){
            throw new OrderException("请选择要购买的商品！");
        }
        // 遍历验库存并锁库存
        lockVos.forEach(lockVo -> {
            this.checkAndLock(lockVo);
        });

        // 判断是否存在验库存并锁库存失败的商品，如果存在则把锁定成功的库存解锁
        if (lockVos.stream().anyMatch(lockVo -> !lockVo.getLock())){
            // 获取锁定成功的库存，解锁
            lockVos.stream().filter(SkuLockVo::getLock).collect(Collectors.toList()).forEach(lockVo -> {
                this.wareSkuMapper.unlock(lockVo.getWareSkuId(), lockVo.getCount());
            });
            return lockVos;
        }

        // 缓存锁定信息到redis，以方便将来解锁库存 或者 减库存
        this.redisTemplate.opsForValue().set(KEY_PREFIX + orderToken, JSON.toJSONString(lockVos), 26, TimeUnit.HOURS);

        // 如果验库存并锁库存成功的情况下，返回null
        return null;
    }

    private void checkAndLock(SkuLockVo lockVo){
        RLock lock = this.redissonClient.getLock(LOCK_PREFIX + lockVo.getSkuId());
        lock.lock();

        try {
            // 1.验库存
            List<WareSkuEntity> wareSkuEntities = this.wareSkuMapper.check(lockVo.getSkuId(), lockVo.getCount());
            // 如果满足条件的库存为空，则验库存失败
            if (CollectionUtils.isEmpty(wareSkuEntities)) {
                lockVo.setLock(false);
                return;
            }

            // 2.锁库存：大数据分析最佳仓库，这里取第一个仓库
            WareSkuEntity wareSkuEntity = wareSkuEntities.get(0);
            if (this.wareSkuMapper.lock(wareSkuEntity.getId(), lockVo.getCount()) == 1) {
                lockVo.setLock(true);
                lockVo.setWareSkuId(wareSkuEntity.getId());
            }
        } finally {
            lock.unlock();
        }
    }

}