package com.ting.websocket.controller;

import com.ting.websocket.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/20
 */
@RestController
public class HelloController {
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping(value = "info")
    public String getInfo() {
        stringRedisTemplate.opsForValue().set("123123435","ceshi1");
        return "hello world";
    }


    @GetMapping(value = "getLocalAddr")
    public String getLocalAddr() {
        return serverConfig.getLocalAddr();
    }

}
