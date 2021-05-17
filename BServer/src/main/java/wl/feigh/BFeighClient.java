package wl.feigh;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("a-server")
public interface BFeighClient {

    @GetMapping("/a/hello")
    String sayHi2A(@RequestParam("hi") String hi);


}
