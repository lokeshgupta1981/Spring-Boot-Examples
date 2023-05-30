package com.howtodoinjava.demo.redis.streams.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisStreamEventConsumer {
    public static void main(String[] args) {
        SpringApplication.run(RedisStreamEventConsumer.class, args);
    }
}