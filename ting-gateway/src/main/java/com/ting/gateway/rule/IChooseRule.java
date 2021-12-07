package com.ting.gateway.rule;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 规则选择
 *
 * @author ting
 * @version 1.0
 * @date 2021/12/7
 */
public interface IChooseRule {


    Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange);

}
