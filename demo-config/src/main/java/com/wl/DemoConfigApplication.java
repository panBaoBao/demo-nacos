package com.wl;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
@MapperScan(basePackages = { "com.wl.mapper" })
public class DemoConfigApplication {
    public static void main(String[] args) {
        try {
            log.info("DemoConfigApplication->start");
            ConfigurableApplicationContext applicationContext = SpringApplication.run(DemoConfigApplication.class);
            log.info("DemoConfigApplication->success");
            String[] beanDefinitionNames = applicationContext.getBeanFactory().getBeanDefinitionNames();
            for(String name : beanDefinitionNames){
                log.info("beanDefinitionName：{}",name);
            }
        }catch (Exception e){
            log.error("DemoConfigApplication->ex",e);
        }
    }
}
