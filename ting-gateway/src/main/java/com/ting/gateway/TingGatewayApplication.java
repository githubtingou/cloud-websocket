package com.ting.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TingGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TingGatewayApplication.class, args);
    }

}
