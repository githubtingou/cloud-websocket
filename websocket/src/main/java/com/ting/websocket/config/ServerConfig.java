package com.ting.websocket.config;

import com.ting.websocket.WebsocketApplication;
import com.ting.websocket.util.IpUtils;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 服务消息
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/21
 */
@Component
public class ServerConfig implements ApplicationListener<WebServerInitializedEvent> {
    private int port;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.port = event.getWebServer().getPort();

    }

    /**
     * 获取服务的ip加端口号
     *
     * @return
     */
    public String getLocalAddr() {
        return IpUtils.getLocalIp() + port;
    }
}
