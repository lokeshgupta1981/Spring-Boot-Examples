package com.howtodoinjava.library.web;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "stock", types = StockClient.class)
public class HttpClientsConfig {

  @Bean
  RestClientHttpServiceGroupConfigurer stockGroup(Environment env) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setReadTimeout(Duration.ofSeconds(2));
    return groups -> groups.filterByName("stock").forEachClient((group, builder) -> builder
        .baseUrl(env.getRequiredProperty("stock.base-url"))
        .requestFactory(requestFactory));
  }
}
