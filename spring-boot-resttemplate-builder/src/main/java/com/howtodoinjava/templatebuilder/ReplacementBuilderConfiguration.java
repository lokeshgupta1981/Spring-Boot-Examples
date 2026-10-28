package com.howtodoinjava.templatebuilder;

import java.time.Duration;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.restclient.autoconfigure.RestTemplateBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("replace-builder")
public class ReplacementBuilderConfiguration {
    @Bean
    RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
        return configurer.configure(new RestTemplateBuilder())
                .connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(3));
    }
}
