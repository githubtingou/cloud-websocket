package com.ting.websocket.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Ip工具类
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/21
 */
@Slf4j
@Configuration
public class IpUtils {

    @Value(value = "server.port")
    private String port;

    /**
     * 获取本地ip地址
     *
     * @return
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                return networkInterface.getInetAddresses().nextElement().getHostAddress();
            }
        } catch (SocketException e) {
            log.error("获取本地IP地址失败");
        }
        return null;


    }

    public static void main(String[] args) {
        System.out.println(getLocalIp());
    }

}
