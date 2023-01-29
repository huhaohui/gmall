package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.exception.OrderException;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.payment.config.AlipayTemplate;
import com.atguigu.gmall.payment.interceptors.LoginInterceptor;
import com.atguigu.gmall.payment.pojo.PaymentInfoEntity;
import com.atguigu.gmall.payment.pojo.UserInfo;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.gmall.payment.vo.PayAsyncVo;
import com.atguigu.gmall.payment.vo.PayVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("pay.html")
    public String pay(@RequestParam("orderToken")String orderToken, Model model){

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getUserId();

        // 根据订单编号查询订单
        OrderEntity orderEntity = this.paymentService.queryOrderByToken(orderToken);
        if (orderEntity == null){
            throw new OrderException("您要支付的订单不存在！");
        }
        // 校验订单是否属于当前用户
        if (userId != orderEntity.getUserId()){
            throw new OrderException("该订单不属于您！");
        }

        // 判断订单状态是否是未支付状态
        if(orderEntity.getStatus() != 0) {
            throw new OrderException("该订单不可以支付！");
        }

        model.addAttribute("orderEntity", orderEntity);

        return "pay";
    }

    @GetMapping("alipay.html")
    @ResponseBody  // html --> xml 以其他视图形式展示方法的范湖结果集
    public Object alipay(@RequestParam("orderToken")String orderToken){

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getUserId();

        // 根据订单编号查询订单
        OrderEntity orderEntity = this.paymentService.queryOrderByToken(orderToken);
        if (orderEntity == null){
            throw new OrderException("您要支付的订单不存在！");
        }
        // 校验订单是否属于当前用户
        if (userId != orderEntity.getUserId()){
            throw new OrderException("该订单不属于您！");
        }

        // 判断订单状态是否是未支付状态
        if(orderEntity.getStatus() != 0) {
            throw new OrderException("该订单不可以支付！");
        }

        try {
            // 调用支付宝的支付接口，打开支付页面
            PayVo payVo = new PayVo();
            payVo.setOut_trade_no(orderToken);
            // 一定不要写订单中的实际金额，一定要写0.01
            payVo.setTotal_amount("0.01");
            payVo.setSubject("谷粒商城支付平台");

            // 去支付时，记录对账信息
            Long payId = this.paymentService.savePaymentInfo(payVo);
            payVo.setPassback_params(payId.toString());

            return this.alipayTemplate.pay(payVo);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        throw new OrderException("打开支付页面失败，请重试！");
    }

    @GetMapping("pay/success")
    public String paySuccess(@RequestParam("out_trade_no")String out_trade_no){
        // TODO: 获取订单编号，查询订单
        System.out.println("同步回调。。。。。。。。。。。。。。。。。。。。。。");
        // 页面跳转
        return "paysuccess";
    }

    @PostMapping("pay/ok")
    @ResponseBody
    public String payOk(PayAsyncVo asyncVo){
        // 1.验签：确保是支付宝发送 failure
        Boolean flag = this.alipayTemplate.checkSignature(asyncVo);
        if (!flag){
            return "failure";
        }

        // 2.校验业务参数：app_id、out_trade_no、total_amount
        String app_id = asyncVo.getApp_id();
        String out_trade_no = asyncVo.getOut_trade_no();
        String total_amount = asyncVo.getTotal_amount();
        String payId = asyncVo.getPassback_params();
        PaymentInfoEntity paymentInfoEntity = this.paymentService.queryPaymentInfoById(payId);
        if (!StringUtils.equals(app_id, this.alipayTemplate.getApp_id()) ||
                !StringUtils.equals(out_trade_no, paymentInfoEntity.getOutTradeNo()) ||
                new BigDecimal(total_amount).compareTo(paymentInfoEntity.getTotalAmount()) != 0){
            return "failure";
        }

        // 3.校验支付状态：TRADE_SUCCESS
        if (!StringUtils.equals("TRADE_SUCCESS", asyncVo.getTrade_status())){
            return "failure";
        }

        // 4.修改对账记录：改为已支付状态
        paymentInfoEntity.setTradeNo(asyncVo.getTrade_no());
        paymentInfoEntity.setCallbackTime(new Date());
        paymentInfoEntity.setCallbackContent(JSON.toJSONString(asyncVo));
        paymentInfoEntity.setPaymentStatus(1);
        if (this.paymentService.updatePaymentInfo(paymentInfoEntity) == 1) {
            // 5.发送消息给mq，将来oms接受消息修改订单状态
            this.rabbitTemplate.convertAndSend("ORDER_EXCHANGE", "order.pay", out_trade_no);
        }

        // 6.返回success
        return "success";
    }
}
