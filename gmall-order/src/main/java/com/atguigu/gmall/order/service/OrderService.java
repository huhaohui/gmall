package com.atguigu.gmall.order.service;

import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.exception.CartException;
import com.atguigu.gmall.common.exception.OrderException;
import com.atguigu.gmall.order.feign.GmallCartClient;
import com.atguigu.gmall.order.feign.GmallPmsClient;
import com.atguigu.gmall.order.feign.GmallSmsClient;
import com.atguigu.gmall.order.feign.GmallUmsClient;
import com.atguigu.gmall.order.feign.GmallWmsClient;
import com.atguigu.gmall.order.interceptors.LoginInterceptor;
import com.atguigu.gmall.order.pojo.OrderConfirmVo;
import com.atguigu.gmall.order.pojo.OrderItemVo;
import com.atguigu.gmall.order.pojo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/28 16:55
 * @Email: 1656311081@qq.com
 */
@Service
public class OrderService {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private GmallWmsClient wmsClient;

    @Autowired
    private GmallUmsClient umsClient;

    @Autowired
    private GmallCartClient cartClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "order:token:";

    public OrderConfirmVo confirm() {

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getUserId();

        // 根据userId查询收货地址
        ResponseVo<List<UserAddressEntity>> addressResponseVo = this.umsClient.queryAddressesByUserId(userId);
        List<UserAddressEntity> addressEntities = addressResponseVo.getData();
        confirmVo.setAddresses(addressEntities);

        // 送货清单
        ResponseVo<List<Cart>> cartResponseVo = this.cartClient.queryCheckedCartsByUserId(userId);
        List<Cart> carts = cartResponseVo.getData();
        if (CollectionUtils.isEmpty(carts)){
            throw new CartException("您没有选中的购物车！");
        }
        // 把购物车集合转化成送货清单集合
        List<OrderItemVo> items = carts.stream().map(cart -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setSkuId(cart.getSkuId());
            orderItemVo.setCount(cart.getCount());

            // 根据skuId查询sku
            ResponseVo<SkuEntity> skuEntityResponseVo = this.pmsClient.querySkuById(cart.getSkuId());
            SkuEntity skuEntity = skuEntityResponseVo.getData();
            if (skuEntity == null) {
                throw new OrderException("您要下单的商品不存在！");
            }
            orderItemVo.setTitle(skuEntity.getTitle());
            orderItemVo.setPrice(skuEntity.getPrice());
            orderItemVo.setWeight(skuEntity.getWeight());
            orderItemVo.setDefaultImage(skuEntity.getDefaultImage());

            // 根据skuId查询销售属性
            ResponseVo<List<SkuAttrValueEntity>> saleAttrResponseVo = this.pmsClient.querySaleAttrValuesBySkuId(cart.getSkuId());
            List<SkuAttrValueEntity> skuAttrValueEntities = saleAttrResponseVo.getData();
            orderItemVo.setSaleAttrs(skuAttrValueEntities);

            // 查询库存
            ResponseVo<List<WareSkuEntity>> wareSkuResponseVo = this.wmsClient.queryWareSkuBySkuId(cart.getSkuId());
            List<WareSkuEntity> wareSkuEntities = wareSkuResponseVo.getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)){
                orderItemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
            }

            // 查询营销信息
            ResponseVo<List<ItemSaleVo>> salesResponseVo = this.smsClient.querySalesBySkuId(cart.getSkuId());
            List<ItemSaleVo> itemSaleVos = salesResponseVo.getData();
            orderItemVo.setSales(itemSaleVos);

            return orderItemVo;
        }).collect(Collectors.toList());
        confirmVo.setItems(items);

        // 根据userId查询用户
        ResponseVo<UserEntity> userEntityResponseVo = this.umsClient.queryUserById(userId);
        UserEntity userEntity = userEntityResponseVo.getData();
        if (userEntity != null) {
            confirmVo.setBounds(userEntity.getIntegration());
        }

        // 防重的唯一标识：页面上 redis
        String orderToken = IdWorker.getIdStr();
        confirmVo.setOrderToken(orderToken);
        this.redisTemplate.opsForValue().set(KEY_PREFIX + orderToken, orderToken);

        return confirmVo;
    }

}
