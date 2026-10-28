package com.howtodoinjava.interceptor;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class ShipmentClientConfig {
  @Bean
  ShipmentStatusClient shipmentStatusClient(RestTemplateBuilder builder) {
    RestTemplate template = builder
        .baseUri("https://shipping.example")
        .additionalInterceptors(new ExchangeLoggingInterceptor())
        .build();
    return new ShipmentStatusClient(template);
  }
}
