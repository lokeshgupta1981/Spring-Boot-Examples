package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.util.CustomRedisSerializer;
import com.howtodoinjava.demo.util.RedisCacheErrorHandler;
import io.lettuce.core.ReadFrom;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Configuration
public class RedisDefaultConfig implements CachingConfigurer {

    @Autowired
    private ConnectionProperties connectionProperties;

    /**
    @Bean(name = "defaultRedisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Add some specific configuration here. Key serializers, etc.
        return template;
    }
     */

    @Bean
    public LettucePoolingClientConfiguration poolingClientConfiguration() {

        GenericObjectPoolConfig poolConfig = connectionProperties.getPoolConfig().genericObjectPoolConfig();

        return LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }

    @Bean(name = "customConnectionFactory")
    public LettuceConnectionFactory connectionFactory(LettucePoolingClientConfiguration poolingClientConfiguration) {

        if (connectionProperties.getRedisConnection().equalsIgnoreCase("standalone")) {
            return new LettuceConnectionFactory(redisStandaloneConfiguration(), poolingClientConfiguration);
        } else {
            return redisSentinelConfiguration();
        }
    }

    @Override
    @Bean
    public RedisCacheManager cacheManager() {
        return RedisCacheManager
                .builder(this.connectionFactory(poolingClientConfiguration()))
                .cacheDefaults(this.cacheConfiguration())
                .build();
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(connectionProperties.getTimeToLive()))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new CustomRedisSerializer()));
    }

    @Bean(name = "customRedisTemplate")
    public RedisTemplate<Object, Object> redisUtilRedisTemplate(
            @Qualifier("customConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setDefaultSerializer(new CustomRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new CustomRedisSerializer());
        redisTemplate.setHashValueSerializer(new CustomRedisSerializer());
        return redisTemplate;
    }

    private RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisProperties redisProperties = connectionProperties.getRedisProperties();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                redisProperties.getHost(), redisProperties.getPort());

        if (StringUtils.hasText(redisProperties.getPassword()))
            redisStandaloneConfiguration.setPassword(redisProperties.getPassword());

        return redisStandaloneConfiguration;
    }

    private LettuceConnectionFactory redisSentinelConfiguration(){

        RedisProperties redisProperties = connectionProperties.getRedisProperties();

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(redisProperties.getSentinel().getMaster());

        redisProperties.getSentinel().getNodes().forEach(s -> sentinelConfig.sentinel(s.split(":")[0],
                Integer.valueOf(s.split(":")[1])
        ));

        if (StringUtils.hasText(redisProperties.getPassword()))
            sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));


        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(redisProperties.getTimeout())
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();

        return new LettuceConnectionFactory(sentinelConfig, clientConfig);

    }
}
