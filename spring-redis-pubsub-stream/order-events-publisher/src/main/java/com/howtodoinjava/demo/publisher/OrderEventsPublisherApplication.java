package com.howtodoinjava.demo.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderEventsPublisherApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderEventsPublisherApplication.class, args);
    }
}