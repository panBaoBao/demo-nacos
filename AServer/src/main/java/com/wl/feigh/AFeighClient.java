package com.wl.feigh;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("b-server")
public interface AFeighClient {

    @GetMapping("/b/hello")
    String sayHi2B(@RequestParam("hi") String hi);


}
