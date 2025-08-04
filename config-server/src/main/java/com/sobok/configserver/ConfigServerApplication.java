package com.sobok.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    // version 1.1.2
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
