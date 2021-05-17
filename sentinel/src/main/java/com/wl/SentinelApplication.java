package com.wl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@Slf4j
public class SentinelApplication {

    public static void main(String[] args) {
        try {
            log.info("SentinelApplication -> start");
            SpringApplication.run(SentinelApplication.class);
            log.info("SentinelApplication -> success");
        }catch (Exception e){
            log.error("SentinelApplication -> ex",e);
        }
    }
}
