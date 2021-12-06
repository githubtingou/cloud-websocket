package com.ting.websocket.socket;

import com.ting.websocket.socket.handle.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * websocket配置类
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/19
 */

@Configuration
@EnableWebSocket
@EnableWebMvc
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandle webSocketHandle;

    public WebSocketConfig(WebSocketHandle webSocketHandle) {
        this.webSocketHandle = webSocketHandle;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketHandle, "/socket/web")
                .setAllowedOrigins("*");
    }


}
