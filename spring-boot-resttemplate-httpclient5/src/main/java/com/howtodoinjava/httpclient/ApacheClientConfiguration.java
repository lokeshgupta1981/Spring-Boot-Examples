package com.howtodoinjava.httpclient;

import java.time.Duration;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class ApacheClientConfiguration {
  @Bean
  PoolingHttpClientConnectionManager connectionManager() {
    return PoolingHttpClientConnectionManagerBuilder.create()
        .setMaxConnTotal(20)
        .setMaxConnPerRoute(10)
        .setDefaultConnectionConfig(ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(3))
            .build())
        .build();
  }

  @Bean(destroyMethod = "close")
  CloseableHttpClient apacheHttpClient(PoolingHttpClientConnectionManager connectionManager) {
    return HttpClients.custom()
        .setConnectionManager(connectionManager)
        .build();
  }

  @Bean
  RestTemplate inventoryRestTemplate(RestTemplateBuilder builder, CloseableHttpClient httpClient,
      @Value("${inventory.base-uri:https://inventory.example}") String baseUri) {
    HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory(httpClient);
    factory.setConnectionRequestTimeout(Duration.ofSeconds(2));
    factory.setReadTimeout(Duration.ofSeconds(5));
    return builder.baseUri(baseUri)
        .requestFactory(() -> factory)
        .build();
  }

  @Bean
  InventoryClient inventoryClient(RestTemplate inventoryRestTemplate) {
    return new InventoryClient(inventoryRestTemplate);
  }
}
