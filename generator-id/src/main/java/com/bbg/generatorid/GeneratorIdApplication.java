package com.bbg.generatorid;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
@MapperScan(basePackages = { "com.bbg.generatorid.dao" })
public class GeneratorIdApplication {

    public static void main(String[] args) {
        try {
            log.info("GeneratorIdApplication->start");
            SpringApplication.run(GeneratorIdApplication.class, args);
            log.info("GeneratorIdApplication->success");
        }catch (Exception e){
            log.error("GeneratorIdApplication-ex",e);
        }
    }

}
