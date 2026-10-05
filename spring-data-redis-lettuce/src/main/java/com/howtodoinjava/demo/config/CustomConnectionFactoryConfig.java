package com.howtodoinjava.demo.config;

import io.lettuce.core.api.StatefulConnection;
import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

/**
 * Replaces the auto-configured connection factory with our own.
 * Active only with the "custom-factory" profile, for example
 * mvn spring-boot:run -Dspring-boot.run.profiles=custom-factory
 */
@Configuration
@Profile("custom-factory")
public class CustomConnectionFactoryConfig {

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {

    GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
    poolConfig.setMaxTotal(16);
    poolConfig.setMaxIdle(8);
    poolConfig.setMinIdle(2);
    poolConfig.setMaxWait(Duration.ofSeconds(2));

    LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
        .poolConfig(poolConfig)
        .commandTimeout(Duration.ofSeconds(2))
        .build();

    RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("localhost", 6379);
    // serverConfig.setPassword("secret");   when the server needs a password

    return new LettuceConnectionFactory(serverConfig, clientConfig);
  }
}
