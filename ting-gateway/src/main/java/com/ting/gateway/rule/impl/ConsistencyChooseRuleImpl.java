package com.ting.gateway.rule.impl;

import com.ting.gateway.common.RedisCommon;
import com.ting.gateway.rule.IChooseRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性规则
 *
 * @author ting
 * @version 1.0
 * @date 2021/12/7
 */
@Service
@Slf4j
public class ConsistencyChooseRuleImpl implements IChooseRule {

    private final StringRedisTemplate stringRedisTemplate;

    public ConsistencyChooseRuleImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        String list = queryParams.getFirst("token");
        TreeMap<Object, Object> entries = (TreeMap)stringRedisTemplate.opsForHash().entries(RedisCommon.RING_HASH_KEY);
        return null;
    }
}
