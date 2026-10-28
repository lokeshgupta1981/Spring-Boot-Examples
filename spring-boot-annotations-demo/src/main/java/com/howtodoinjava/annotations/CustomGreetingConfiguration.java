package com.howtodoinjava.annotations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "greeting", name = "custom", havingValue = "true")
public class CustomGreetingConfiguration {
    @Bean
    GreetingService customGreetingService() {
        return () -> "Hello from the application";
    }
}
