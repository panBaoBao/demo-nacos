package wl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import wl.feigh.BFeighClient;

@RestController
@Slf4j
public class BController {
    @Autowired
    private BFeighClient bFeighClient;

    @GetMapping("hi/{hi}")
    public String sayHi(@PathVariable("hi") String hi){
        return hi;
    }

    @GetMapping("hi2a/{hi}")
    public String sayHi2A(@PathVariable("hi") String hi){
        log.info("sayHi2A hi:{}",hi);
        return bFeighClient.sayHi2A(hi);
    }

    @GetMapping("/hello")
    public String hello(String hi){
        log.info("hello hi:{}",hi);
        return "b say " + hi;
    }
}
