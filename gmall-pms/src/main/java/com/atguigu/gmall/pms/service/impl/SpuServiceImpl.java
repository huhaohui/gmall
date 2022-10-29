package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.SpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuByCidAndPage(Long cid, PageParamVo paramVo) {
        QueryWrapper<SpuEntity> spuEntityQueryWrapper = new QueryWrapper<>();
        if(cid!=0){
            spuEntityQueryWrapper.eq("category_id",cid);
        }

        String key = paramVo.getKey();
        if (StringUtils.isNotEmpty(key)) {
            spuEntityQueryWrapper.and(a -> a.eq("id", key).or().like("name", key));
//            spuEntityQueryWrapper.eq("id", key).or().like("name", key);
        }
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                spuEntityQueryWrapper
        );

        return new PageResultVo(page);
    }

}