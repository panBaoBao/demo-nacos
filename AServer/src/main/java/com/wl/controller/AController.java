package com.wl.controller;

import com.wl.feigh.AFeighClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AController {
    @Autowired
    private AFeighClient aFeighClient;

    @GetMapping("/hi/{hi}")
    public String sayHi(@PathVariable("hi") String hi){
        return hi;
    }


    @GetMapping("/hi2b/{hi}")
    public String sayHi2B(@PathVariable("hi")String hi){
        log.info("sayHi2B hi:{}",hi);
        return aFeighClient.sayHi2B(hi);
    }

    @GetMapping("/hello")
    public String hello(String hi){
        log.info("hello hi:{}",hi);
        return "a say " + hi;
    }
}
