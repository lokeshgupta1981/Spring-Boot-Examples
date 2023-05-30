package com.howtodoinjava.demo.redis.streams.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisStreamEventProducer {
    public static void main(String[] args) {
        SpringApplication.run(RedisStreamEventProducer.class, args);
    }
}