package com.atguigu.gmall.sms.service.impl;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.mapper.SkuFullReductionMapper;
import com.atguigu.gmall.sms.mapper.SkuLadderMapper;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionMapper reductionMapper;

    @Autowired
    private SkuLadderMapper ladderMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional
    public void saveSales(SkuSaleVo saleVo) {
        // 3.1 保存积分优惠表
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(saleVo,skuBoundsEntity);
        List<Integer> work = saleVo.getWork();
        if (!CollectionUtils.isEmpty(work) && work.size() == 4){
            skuBoundsEntity.setWork(work.get(3) * 8 + work.get(2) * 4 + work.get(1)* 2 + work.get(0));
        }
        this.save(skuBoundsEntity);
        // 3.2 保存满减优惠表
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(saleVo, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(saleVo.getFullAddOther());
        this.reductionMapper.insert(skuFullReductionEntity);
        // 3.3 保存打折优惠表
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(saleVo, skuLadderEntity);
        skuLadderEntity.setAddOther(saleVo.getLadderAddOther());
        this.ladderMapper.insert(skuLadderEntity);
    }

}