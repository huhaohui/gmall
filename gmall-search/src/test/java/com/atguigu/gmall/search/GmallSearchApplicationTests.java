package com.atguigu.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.util.List;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2023/1/7 21:11
 * @Email: 1796235969@qq.com
 */

@SpringBootTest
public class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private GmallPmsClient pmsClient;

    @Test
    void contextLoads() {
        //		IndexOperations indexOps = this.restTemplate.indexOps(Goods.class);
//		indexOps.create();
//		indexOps.putMapping(indexOps.createMapping());
        ResponseVo<List<SpuEntity>> listResponseVo = this.pmsClient.querySpuByPageJson(new PageParamVo(1, 10, null));
        List<SpuEntity> spuEntities = listResponseVo.getData();
        spuEntities.forEach(System.out::println);
    }


}
