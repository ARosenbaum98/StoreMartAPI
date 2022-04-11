package com.storemart.v3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
public class V3Application {

    public static void main(String[] args) {
        SpringApplication.run(V3Application.class, args);
    }

}
