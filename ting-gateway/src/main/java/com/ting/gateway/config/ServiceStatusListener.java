package com.ting.gateway.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ting.gateway.common.RedisCommon;
import com.ting.gateway.utils.ConsistencyHashUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
public class ServiceStatusListener implements ApplicationListener<ApplicationStartedEvent> {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ConsistencyHashUtil consistencyHashUtil;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // nacos配置中心地址
        String nacosServerAddr = null;
        // nacos命名中心
        String nacosNamespace = null;
        // 监听的服务名
        String nacosServiceName = null;
        // 监听的分组名称
        String nacosGroupName = "DEFAULT_GROUP";


        final MutablePropertySources propertySources = event.getApplicationContext().getEnvironment().getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource instanceof OriginTrackedMapPropertySource) {
                final OriginTrackedMapPropertySource mapPropertySource = (OriginTrackedMapPropertySource) propertySource;
                final Map<String, Object> map = mapPropertySource.getSource();
                if (map.containsKey((NacosDiscoveryProperties.PREFIX + ".serverAddr"))
                        || map.containsKey((NacosDiscoveryProperties.PREFIX + ".server-addr"))) {

                    nacosServerAddr = Optional.ofNullable(map.get(NacosDiscoveryProperties.PREFIX + ".serverAddr"))
                            .orElse(map.get(NacosDiscoveryProperties.PREFIX + ".server-addr"))
                            .toString();
                }
                if (map.containsKey(NacosDiscoveryProperties.PREFIX + ".namespace")) {
                    nacosNamespace = String.valueOf(map.get(NacosDiscoveryProperties.PREFIX + ".namespace"));
                }

                if (map.containsKey("listener.service-name")) {
                    nacosServiceName = String.valueOf(map.get("listener.service-name"));
                }
                if (map.containsKey("listener.service-name")) {
                    nacosServiceName = String.valueOf(map.get("listener.service-name"));
                }
                if (map.containsKey(NacosDiscoveryProperties.PREFIX + ".group")) {
                    nacosGroupName = String.valueOf(map.get(NacosDiscoveryProperties.PREFIX + ".group"));
                }


            }

        }

        try {
            naming(nacosServerAddr, nacosNamespace, nacosServiceName, nacosGroupName);
        } catch (Exception e) {
            log.error("监听服务异常：", e);
        }

    }

    /**
     * 监听服务
     *
     * @param nacosServerAddr  nacos配置中心地址
     * @param nacosNamespace   nacos命名中心
     * @param nacosServiceName 监听的服务名
     * @param groupName        分组名称
     */
    public void naming(String nacosServerAddr, String nacosNamespace, String nacosServiceName, String groupName) throws Exception {

        Properties properties = System.getProperties();
        properties.setProperty("serverAddr", nacosServerAddr);
        properties.setProperty("namespace", nacosNamespace);
        NamingService naming = NamingFactory.createNamingService(properties);
        naming.subscribe(nacosServiceName, groupName, event -> {
            if (event instanceof NamingEvent) {
                log.warn("监听{}分组下{}服务:", groupName, nacosServiceName);
                NamingEvent namingEvent = (NamingEvent) event;
                List<Instance> instances = namingEvent.getInstances();
                List<String> ips = instances.stream()
                        .map(instance -> instance.getIp() + ":" + instance.getPort())
                        .collect(Collectors.toList());
                // 找到下线的服务，删掉对应的数据
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisCommon.WEBSOCKET_KEY))) {

                }

                if (!CollectionUtils.isEmpty(ips)) {
                    consistencyHashUtil.getHashRing(ips);
                }


            }
        });

    }
}
