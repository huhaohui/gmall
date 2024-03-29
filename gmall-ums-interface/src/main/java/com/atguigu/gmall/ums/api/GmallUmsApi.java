package com.atguigu.gmall.ums.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/16 18:19
 * @Email: 1656311081@qq.com
 */
public interface GmallUmsApi {

    @GetMapping("ums/user/{id}")
    @ApiOperation("详情查询")
    ResponseVo<UserEntity> queryUserById(@PathVariable("id") Long id);

    @GetMapping("ums/user/query")
    @ApiOperation("查询用户")
    ResponseVo<UserEntity> queryUser(
            @RequestParam("loginName")String loginName,
            @RequestParam("password")String password);

    @PostMapping("ums/user/register")
    @ApiOperation("用户注册")
    ResponseVo register(UserEntity userEntity, @RequestParam("code")String code);

    @GetMapping("ums/user/check/{data}/{type}")
    @ApiOperation("校验数据是否可用")
    ResponseVo<Boolean> checkData(@PathVariable("data")String data, @PathVariable("type")Integer type);

    @GetMapping("ums/useraddress/user/{userId}")
    ResponseVo<List<UserAddressEntity>> queryAddressesByUserId(@PathVariable("userId")Long userId);

}
