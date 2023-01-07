package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Description:
 * @Author: xionghu514
 * @Date: 2022/11/8 16:10
 * @Email: 1796235969@qq.com
 */
@Data
public class SearchAttrValueVo {

    @Field(type = FieldType.Long)
    private Long attrId; // 规格参数 id
    @Field(type = FieldType.Keyword)
    private String attrName; // 规格参数 名称
    @Field(type = FieldType.Keyword)
    private String attrValue; // 规格参数 值

}

