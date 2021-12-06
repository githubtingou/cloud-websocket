package com.ting.gateway.config;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * nacos 服务监听
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/23
 */
@Component
@Slf4j
public class ServiceStatusListener implements ApplicationRunner {
    @Value(value = "spring.cloud.nacos.discovery.serverAddr")
    private String nacosServerAddr;
    @Value(value = "spring.cloud.nacos.discovery.namespace")
    private String nacosNamespace;
    @Value(value = "listener.serviceName")
    private String nacosServiceName;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Properties properties = System.getProperties();
        properties.setProperty("serverAddr", nacosServerAddr);
        properties.setProperty("namespace", nacosNamespace);
        NamingService naming = NamingFactory.createNamingService(properties);
        naming.subscribe(nacosServiceName, event -> {
            if (event instanceof NamingEvent) {
                log.warn("{}服务监听:", nacosServiceName);
                NamingEvent namingEvent = (NamingEvent) event;
                List<Instance> instances = namingEvent.getInstances();
                List<String> ips = instances.stream()
                        .map(instance -> instance.getIp() + ":" + instance.getPort())
                        .collect(Collectors.toList());

            }
        });

    }
}
