package com.ting.websocket.config;

import com.ting.websocket.common.RabbitmqCommon;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ting
 * @version 1.0
 * @date 2021/11/21
 */
@Configuration
public class RabbitmqConfig {

    @Autowired
    private ServerConfig serverConfig;

    public Queue queueWebsocket() {
        return new Queue(serverConfig.getLocalAddr(), true);
    }

    @Bean
    public TopicExchange topicWebsocketExchange() {
        return new TopicExchange(RabbitmqCommon.WEB_SOCKET_TOPIC, true, false);
    }

    @Bean
    public Binding bindingWebsocket() {
        return BindingBuilder.bind(queueWebsocket()).to(topicWebsocketExchange()).with(RabbitmqCommon.WEB_SOCKET_ROUTING_KEY);
    }
}
