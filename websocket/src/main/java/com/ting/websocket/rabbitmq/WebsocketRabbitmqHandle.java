package com.ting.websocket.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.ting.websocket.dto.MsgDTO;
import com.ting.websocket.socket.handle.WebSocketHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @author ting
 * @version 1.0
 * @date 2021/11/21
 */
@Component
@Slf4j
public class WebsocketRabbitmqHandle {


    @RabbitListener(queues = "#queueWebsocket()")
    public void webSocketHandle(@Payload String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTa) {
        log.info("webSocket消息处理:{}", msg);
        final MsgDTO msgDTO = JSON.parseObject(msg, MsgDTO.class);
        final WebSocketSession webSocketSession = WebSocketHandle.sessionMap.get(msgDTO.getReceiveUser());
        TextMessage textMessage = new TextMessage(msgDTO.getContent());
        try {
            webSocketSession.sendMessage(textMessage);
            log.info("消息发送成功");
        } catch (IOException e) {
            log.error("消息发送失败", e);
        }

        try {
            channel.basicAck(deliveryTa, false);
            log.info("消息消费成功");
        } catch (IOException e) {
            log.error("消息消费失败");
        }
    }
}
