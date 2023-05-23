package com.howtodoinjava.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConnectionProperties {

    @Value("${default.redis.connection:standalone}")
    private String redisConnection;

    @Value("${default.redis.time-to-live:60}")
    private Integer timeToLive;

    @Autowired
    private RedisProperties redisProperties;

    @Autowired
    private ConnectionPoolProperties poolConfig;
}
