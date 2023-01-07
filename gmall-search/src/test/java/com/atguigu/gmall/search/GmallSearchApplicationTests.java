package com.atguigu.gmall.search;

import com.atguigu.gmall.search.pojo.Goods;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;

@SpringBootTest
class GmallSearchApplicationTests {

	@Autowired
	private ElasticsearchRestTemplate restTemplate;

	@Test
	void contextLoads() {
		IndexOperations indexOps = this.restTemplate.indexOps(Goods.class);
		indexOps.create();
		indexOps.putMapping(indexOps.createMapping());
	}

}
