package wl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class BServerApplication {

    public static void main(String[] args) {
        try {
            log.info("b-server->start");
            SpringApplication.run(BServerApplication.class);
            log.info("b-server->start success");
        }catch (Exception e){
            log.error("b-server start ex",e);
        }
    }
}
