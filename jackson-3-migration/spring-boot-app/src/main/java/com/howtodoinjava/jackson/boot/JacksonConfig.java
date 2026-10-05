package com.howtodoinjava.jackson.boot;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;

@Configuration
public class JacksonConfig {

  // Spring Boot 3: Jackson2ObjectMapperBuilderCustomizer
  @Bean
  JsonMapperBuilderCustomizer strictReading() {
    return builder -> builder.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }
}
