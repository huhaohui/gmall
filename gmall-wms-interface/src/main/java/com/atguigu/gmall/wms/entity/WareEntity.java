package com.atguigu.gmall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 仓库信息
 * 
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2022-10-26 16:38:45
 */
@Data
@TableName("wms_ware")
public class WareEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 仓库名
	 */
	private String name;
	/**
	 * 仓库地址
	 */
	private String address;
	/**
	 * 区域编码
	 */
	private String areacode;

}
