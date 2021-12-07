package com.ting.gateway.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;

/**
 * 负责均衡
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/28
 */
@Component
@Slf4j
public class LbServiceHandle extends ReactiveLoadBalancerClientFilter {

    private final LoadBalancerClientFactory clientFactory;

    private final LoadBalancerProperties properties;

    private final StringRedisTemplate stringRedisTemplate;

    public LbServiceHandle(LoadBalancerClientFactory clientFactory, LoadBalancerProperties properties, StringRedisTemplate stringRedisTemplate) {
        super(clientFactory, properties);
        this.clientFactory = clientFactory;
        this.properties = properties;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url != null && ("ws://".equals(url.getScheme()) || ("wss://".equals(url.getScheme())))) {
            log.info("处理webSocket请求");
            if (!isContainToken(exchange)) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                log.warn("请求没有token");
                String data = "请求没有token!";
                DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
                return response.writeWith(Mono.just(wrap));
            }


        }

        // 处理ws 和wss方式
//        if (PathPattern){
//            // 自定义
//        }
        return super.filter(exchange, chain);
    }


    @SuppressWarnings("deprecation")
    private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
        URI uri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        Map<String, String> stringMap = queryParams.toSingleValueMap();
        stringMap.get("token");
        ReactorLoadBalancer<ServiceInstance> loadBalancer = this.clientFactory
                .getInstance(uri.getHost(), ReactorServiceInstanceLoadBalancer.class);
        if (loadBalancer == null) {
            throw new NotFoundException("No loadbalancer available for " + uri.getHost());
        }
        return loadBalancer.choose(createRequest());
    }

    @SuppressWarnings("deprecation")
    private Request createRequest() {
        return ReactiveLoadBalancer.REQUEST;
    }

    private Boolean isContainToken(ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        return queryParams.containsKey("token");

    }
}
