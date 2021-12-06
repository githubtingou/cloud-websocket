package com.ting.websocket.socket.handle;

import com.ting.websocket.common.RedisKeyCommon;
import com.ting.websocket.config.ServerConfig;
import com.ting.websocket.dto.MsgDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 页面配置类
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/19
 */
@Service
@Slf4j
public class WebSocketHandle extends TextWebSocketHandler {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ServerConfig serverConfig;

    public static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 创建连接后
     *
     * @param session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        final String localAddr = serverConfig.getLocalAddr();
        log.info("在：{}创建连接：{}", localAddr, session.getId());
        final InetSocketAddress localAddress = session.getLocalAddress();
        sessionMap.put(session.getId(), session);

        List<String> sessionIdList = (List<String>) stringRedisTemplate.opsForHash().get(RedisKeyCommon.WEB_SOCKET_KEY, localAddr);
        if (CollectionUtils.isEmpty(sessionIdList)) {
            stringRedisTemplate.opsForHash().put(RedisKeyCommon.WEB_SOCKET_KEY, localAddr, new ArrayList<String>() {{
                add(session.getId());
            }});
        } else {
            sessionIdList.add(session.getId());
            stringRedisTemplate.opsForHash().put(RedisKeyCommon.WEB_SOCKET_KEY, localAddr, sessionIdList);
        }


        TextMessage textMessage = new TextMessage(session.getId());
        try {
            session.sendMessage(textMessage);
        } catch (IOException e) {
            log.error("消息发送失败");
        }
        System.out.println(localAddress);

    }

    /**
     * 消息处理
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.setTextMessageSizeLimit(20 * 1024);
        log.info("webSession_id:{},发送的消息体：{}", session.getId(), message.getPayload());
        session.sendMessage(message);

    }

    /**
     * websocket发生已异常
     *
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        final String localAddr = serverConfig.getLocalAddr();
        log.error("webSession_id:{}发生异常：", session.getId(), exception);
        session.close();
        sessionMap.remove(session.getId());
        List<String> sessionIdList = (List<String>) stringRedisTemplate.opsForHash().get(RedisKeyCommon.WEB_SOCKET_KEY, localAddr);
        if (!CollectionUtils.isEmpty(sessionIdList)) {
            sessionIdList.removeIf(s -> s.equals(session.getId()));
            stringRedisTemplate.opsForHash().put(RedisKeyCommon.WEB_SOCKET_KEY, localAddr, sessionIdList);
        }

    }

    /**
     * webSocket消息关闭
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        final String localAddr = serverConfig.getLocalAddr();
        final Map<String, Object> attributes = session.getAttributes();
        log.info("webSession_id:{}关闭,状态码：{}", session.getId(), status.getCode());
        session.close();
        sessionMap.remove(session.getId());
        List<String> sessionIdList = (List<String>) stringRedisTemplate.opsForHash().get(RedisKeyCommon.WEB_SOCKET_KEY, localAddr);
        if (!CollectionUtils.isEmpty(sessionIdList)) {
            sessionIdList.removeIf(s -> s.equals(session.getId()));
            stringRedisTemplate.opsForHash().put(RedisKeyCommon.WEB_SOCKET_KEY, localAddr, sessionIdList);
        }
    }

    /**
     * 消息发送
     *
     * @param msgDTO
     * @return
     * @throws Exception
     */
    public String sendMeg(MsgDTO msgDTO) throws Exception {
        final String receiveUser = msgDTO.getReceiveUser();
        final WebSocketSession session = sessionMap.get(receiveUser);
        if (session == null) {

            log.warn("当前用户下线,{}", receiveUser);
            return "用户已下线";
        }
        TextMessage textMessage = new TextMessage(msgDTO.getContent());
        if (session.isOpen()) {
            handleTextMessage(session, textMessage);
        }
        return "success";


    }
}
