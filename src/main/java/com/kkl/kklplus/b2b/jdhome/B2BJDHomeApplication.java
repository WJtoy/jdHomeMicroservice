package com.kkl.kklplus.b2b.jdhome;

import com.kkl.kklplus.b2b.jdhome.config.B2BJdProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableEurekaClient
@SpringBootApplication
@EnableConfigurationProperties(B2BJdProperties.class)
@EnableFeignClients
public class B2BJDHomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(B2BJDHomeApplication.class, args);
    }
}
