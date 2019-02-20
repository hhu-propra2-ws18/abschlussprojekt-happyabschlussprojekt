package com.propra.happybay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HappybayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappybayApplication.class, args);
    }

}

