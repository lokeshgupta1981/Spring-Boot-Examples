package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.model.Recipe;
import java.time.Duration;
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  /**
   * A typed template that stores Recipe objects as JSON under String keys.
   * The auto-configured RedisTemplate<Object, Object> and StringRedisTemplate stay available.
   */
  @Bean
  public RedisTemplate<String, Recipe> recipeRedisTemplate(RedisConnectionFactory connectionFactory) {

    RedisTemplate<String, Recipe> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new JacksonJsonRedisSerializer<>(Recipe.class));
    template.setHashValueSerializer(new JacksonJsonRedisSerializer<>(Recipe.class));
    return template;
  }

  /**
   * Fine-tunes the auto-configured Lettuce client without replacing the connection factory.
   * A command that takes longer than 2 seconds fails with a QueryTimeoutException.
   */
  @Bean
  public LettuceClientConfigurationBuilderCustomizer lettuceCustomizer() {
    return builder -> builder
        .commandTimeout(Duration.ofSeconds(2))
        .clientName("recipe-app");
  }
}
