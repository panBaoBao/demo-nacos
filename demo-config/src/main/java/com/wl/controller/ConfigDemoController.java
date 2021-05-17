package com.wl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@Slf4j
@RestController
public class ConfigDemoController {
    @Value("${user.name}")
    private String userName;
    @Value(value = "${user.age}")
    private String age;

    @GetMapping("info")
    public String info(){

        return "userName:" + userName + ",age:" + age;
    }

}
