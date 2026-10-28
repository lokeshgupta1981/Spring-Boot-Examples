package com.howtodoinjava.templatebuilder;

import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PlatformClientConfiguration {
    @Bean
    RestTemplateCustomizer platformHeader() {
        return template -> template.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("X-Platform", "storefront");
            return execution.execute(request, body);
        });
    }
}
