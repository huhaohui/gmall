package com.atguigu.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.feign.GmallWmsClient;
import com.atguigu.gmall.cart.interceptors.LoginInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.exception.CartException;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/24 15:53
 * @Email: 1656311081@qq.com
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CartAsyncService asyncService;

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private GmallWmsClient wmsClient;

    private static final String KEY_PREFIX = "cart:info:";
    private static final String PRICE_PREFIX = "cart:price:";

    public void saveCart(Cart cart) {
        // 1.获取登录状态
        String userId = getUserId();

        // 2.判断当前用户的购物车是否包含该商品 Map<userId/userKey, Map<skuId, cartJson>>
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userId);

        // Map<skuId, cartJson>
        String skuId = cart.getSkuId().toString();
        BigDecimal count = cart.getCount(); // 本次新增数量
        if (hashOps.hasKey(skuId)) {
            // 包含则更新数量
            String cartJson = hashOps.get(skuId).toString();
            // 反序列化为购物车对象
            cart = JSON.parseObject(cartJson, Cart.class);
            // 数据库中的数量累加新增的数量
            cart.setCount(cart.getCount().add(count));
            // 更新到数据库 redis mysql
            this.asyncService.updateCart(userId, skuId, cart);
        } else {
            // 不包含则新增记录 skuId count
            cart.setUserId(userId);
            cart.setCheck(true);

            // 根据skuId查询sku
            ResponseVo<SkuEntity> skuEntityResponseVo = this.pmsClient.querySkuById(cart.getSkuId());
            SkuEntity skuEntity = skuEntityResponseVo.getData();
            if (skuEntity == null) {
                throw new CartException("您要加入购物车的商品不存在！");
            }
            cart.setTitle(skuEntity.getTitle());
            cart.setPrice(skuEntity.getPrice());
            cart.setDefaultImage(skuEntity.getDefaultImage());

            // 根据skuId查询当前sku的销售属性
            ResponseVo<List<SkuAttrValueEntity>> saleAttrsResponseVo = this.pmsClient.querySaleAttrValuesBySkuId(cart.getSkuId());
            List<SkuAttrValueEntity> skuAttrValueEntities = saleAttrsResponseVo.getData();
            cart.setSaleAttrs(JSON.toJSONString(skuAttrValueEntities));

            // 根据skuId查询库存
            ResponseVo<List<WareSkuEntity>> wareSkuResponseVo = this.wmsClient.queryWareSkuBySkuId(cart.getSkuId());
            List<WareSkuEntity> wareSkuEntities = wareSkuResponseVo.getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)){
                cart.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
            }

            // 根据skuId查询营销信息
            ResponseVo<List<ItemSaleVo>> salesResponseVo = this.smsClient.querySalesBySkuId(cart.getSkuId());
            List<ItemSaleVo> itemSaleVos = salesResponseVo.getData();
            cart.setSales(JSON.toJSONString(itemSaleVos));

            // 保存到数据库 redis mysql
            this.asyncService.insertCart(cart);
        }
        hashOps.put(skuId, JSON.toJSONString(cart));
    }


    private String getUserId() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userId = userInfo.getUserKey();
        if (userInfo.getUserId() != null) {
            userId = userInfo.getUserId().toString();
        }
        return userId;
    }

    public Cart queryCartBySkuId(Long skuId) {
        // 获取登录状态
        String userId = this.getUserId();

        // 内层的map<skuId, cartJson>
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userId);
        if (!hashOps.hasKey(skuId.toString())){
            throw new CartException("您的购物车中没有该商品！");
        }

        String cartJson = hashOps.get(skuId.toString()).toString();
        return JSON.parseObject(cartJson, Cart.class);
    }


    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        // 1.以userKey查询未登录的购物车
        String userKey = userInfo.getUserKey();
        BoundHashOperations<String, Object, Object> unloginHashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userKey);
        List<Object> cartJsons = unloginHashOps.values();
        // 把未登录购物车的json字符串集合 转化成 购物车对象集合
        List<Cart> unloginCarts = null;
        if (!CollectionUtils.isEmpty(cartJsons)){
            unloginCarts = cartJsons.stream().map(cartJson -> {
                Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                return cart;
            }).collect(Collectors.toList());
        }

        // 2.判断是否登录（userId == null），如果未登录则直接返回未登录的购物车
        Long userId = userInfo.getUserId();
        if (userId == null) {
            return unloginCarts;
        }

        // 3.把未登录的购物车 合并到 已登录的购物车（userId）中去
        BoundHashOperations<String, Object, Object> loginHashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userId);
        // 如果存在未登录的购物车 则 合并到已登录的购物车中
        if (!CollectionUtils.isEmpty(unloginCarts)){
            unloginCarts.forEach(cart -> { // 遍历未登录购物车中的一条记录
                // 判断已登录的购物车中是否包含该记录
                String skuId = cart.getSkuId().toString();
                BigDecimal count = cart.getCount(); // 未登录购物车中数量
                if (loginHashOps.hasKey(skuId)){
                    // 如果包含则更新数量
                    String cartJson = loginHashOps.get(skuId).toString(); // 已登录购物车的json字符串
                    cart = JSON.parseObject(cartJson, Cart.class);
                    cart.setCount(cart.getCount().add(count));
                    // 保存到数据库
                    this.asyncService.updateCart(userId.toString(), skuId, cart);
                } else {
                    // 如果不包含则新增记录
                    cart.setId(null);
                    cart.setUserId(userId.toString());
                    this.asyncService.insertCart(cart);
                }
                loginHashOps.put(skuId, JSON.toJSONString(cart));
            });

            // 4.清空未登录的购物车
            this.redisTemplate.delete(KEY_PREFIX + userKey);
            this.asyncService.deleteByUserId(userKey);
        }

        // 5.返回合并后的购物车给用户
        List<Object> loginCartJsons = loginHashOps.values();
        if (!CollectionUtils.isEmpty(loginCartJsons)){
            return loginCartJsons.stream().map(cartJson -> {
                Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                return cart;
            }).collect(Collectors.toList());
        }
        return null;
    }

    public void updateNum(Cart cart) {
        // 获取登录状态
        String userId = this.getUserId();

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userId);
        String skuId = cart.getSkuId().toString();
        BigDecimal count = cart.getCount();
        if (hashOps.hasKey(skuId)){
            String cartJson = hashOps.get(skuId).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCount(count);

            hashOps.put(skuId, JSON.toJSONString(cart));
            this.asyncService.updateCart(userId, skuId, cart);
        }
    }

    public void updateStatus(Cart cart) {
        // 获取登录状态
        String userId = this.getUserId();

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userId);
        String skuId = cart.getSkuId().toString();
        Boolean check = cart.getCheck();
        if (hashOps.hasKey(skuId)){
            String cartJson = hashOps.get(skuId).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCheck(check);

            hashOps.put(skuId, JSON.toJSONString(cart));
            this.asyncService.updateCart(userId, skuId, cart);
        }
    }

    public void deleteCart(Long skuId) {
        // 获取登录状态
        String userId = this.getUserId();

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userId);

        hashOps.delete(skuId.toString());
        this.asyncService.deleteByUserIdAndSkuId(userId, skuId);
    }
}
