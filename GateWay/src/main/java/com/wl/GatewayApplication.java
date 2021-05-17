package com.wl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class GatewayApplication {
    public static void main(String[] args) {
        try {
            log.info("gateway start");
            SpringApplication.run(GatewayApplication.class);
            log.info("gateway success");
        }catch (Exception e){
            log.error("gateway ex",e);
        }
    }
}
