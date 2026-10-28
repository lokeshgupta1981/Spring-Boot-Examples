package com.howtodoinjava.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsPolicy implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/reports/**")
                .allowedOrigins("https://admin.example")
                .allowedMethods("GET")
                .allowedHeaders("X-Request-Id");
    }
}
