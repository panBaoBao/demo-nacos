package com.wl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class AServerApplication {

    public static void main(String[] args) {
        try {
            log.info("a-server->start");
            SpringApplication.run(AServerApplication.class);
            log.info("a-server->start success");
        }catch (Exception e){
            log.error("a-server start ex",e);
        }
    }
}
