//package com.ting.websocket.config;
//
//import com.ting.websocket.common.RabbitmqCommon;
//import org.checkerframework.checker.units.qual.A;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//
///**
// * @author ting
// * @version 1.0
// * @date 2021/11/21
// */
//@Configuration
//public class RabbitmqConfig {
//
//    @Autowired
//    private RabbitAdmin rabbitAdmin;
//
//    @Autowired
//    private ServerConfig serverConfig;
//
//    public Queue queueWebsocket() {
//        final Queue queue = new Queue(serverConfig.getLocalAddr(), true);
//        rabbitAdmin.declareQueue(queue);
//        return queue;
//    }
//
//    @Bean
//    public TopicExchange topicWebsocketExchange() {
//        final TopicExchange topicExchange = new TopicExchange(RabbitmqCommon.WEB_SOCKET_TOPIC, true, false);
//        rabbitAdmin.declareExchange(topicExchange);
//        return topicExchange;
//    }
//
//    @Bean
//    public Binding bindingWebsocket() {
//        final Binding binding = BindingBuilder.bind(queueWebsocket())
//                .to(topicWebsocketExchange())
//                .with(RabbitmqCommon.WEB_SOCKET_ROUTING_KEY);
//        rabbitAdmin.declareBinding(binding);
//        return binding;
//    }
//
//    @Bean
//    public RabbitAdmin rabbitAdmin(CachingConnectionFactory factoryBean) {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(factoryBean);
//        // 显示声明队列
//        rabbitAdmin.setAutoStartup(true);
//        return rabbitAdmin;
//    }
//}
