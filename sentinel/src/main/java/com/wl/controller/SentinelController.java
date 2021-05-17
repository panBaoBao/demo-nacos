package com.wl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SentinelController {

    @GetMapping("hi/{hi}")
    public String hi(@PathVariable("hi") String hi){
        return hi;
    }

}
