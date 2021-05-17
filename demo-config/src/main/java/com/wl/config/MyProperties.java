package com.wl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "prop")
@Data
public class MyProperties {

    private Boolean flag;

    private String msg;

}
