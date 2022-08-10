package com.howtodoinjava.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.howtodoinjava.app.service.CustomService;
import com.howtodoinjava.app.service.CustomServiceImpl;

@Configuration
@ConditionalOnClass(CustomService.class)
public class CustomAutoConfiguration {

    @Bean
    public CustomService customService() {
        return new CustomServiceImpl();
    }
}