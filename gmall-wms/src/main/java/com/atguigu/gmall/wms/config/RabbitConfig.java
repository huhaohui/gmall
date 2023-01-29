package com.atguigu.gmall.wms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author: huhaohui
 * @Date: 2023/1/11 16:43
 * @Email: 1656311081@qq.com
 */
@Configuration
@Slf4j
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct // 构造方法执行之后就会执行
    // @PreDestroy // 对象销毁之前执行   <bean id="" class="" init-method="" destroy-method=""/>
    public void init(){
        // 确认消息是否到达交换机的回调，不管消息有没有到达交换机都会执行
        this.rabbitTemplate.setConfirmCallback((@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) -> {
            if (ack){
                // 记录日志 也可以记录到数据库 将来重发
                log.info("消息已经到达交换机。");
            } else {
                log.error("消息没有到达交换机。原因：{}", cause);
            }
        });
        // 确认消息是否到达队列的回调，只有消息没有到达队列才会执行
        this.rabbitTemplate.setReturnCallback((Message message, int replyCode, String replyText, String exchange, String routingKey) -> {
            log.error("消息没有到达队列。交换机：{}，路由键：{}，消息内容：{}，状态码：{}，状态文本：{}",
                    exchange, routingKey, new String(message.getBody()), replyCode, replyText);
        });
    }

    /**
     * 业务交换机：ORDER_EXCHANGE
     */

    /**
     * 延时队列：STOCK_TTL_QUEUE
     */
    @Bean
    public Queue ttlQueue(){
        return QueueBuilder.durable("STOCK_TTL_QUEUE").ttl(100000)
                .deadLetterExchange("ORDER_EXCHANGE").deadLetterRoutingKey("stock.unlock").build();
    }

    /**
     * 把延时队列绑定到业务交换机：stock.ttl
     */
    @Bean
    public Binding ttlBinding(){
        return new Binding("STOCK_TTL_QUEUE", Binding.DestinationType.QUEUE, "ORDER_EXCHANGE", "stock.ttl", null);
    }

    /**
     * 死信交换机：ORDER_EXCHANGE
     */

    /**
     * 死信队列：STOCK_UNLOCK_QUEUE
     */

    /**
     * 把死信队列绑定到死信交换机：stock.unlock
     */
}
