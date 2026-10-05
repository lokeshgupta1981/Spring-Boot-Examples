package com.howtodoinjava.boot4;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.FilteredHostException;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.InetAddressFilter;
import org.springframework.web.client.RestClient;

class SsrfFilterTest {

  @Test
  void externalAddressesFilterBlocksLoopback() {
    HttpClientSettings settings = HttpClientSettings.defaults()
        .withInetAddressFilter(InetAddressFilter.externalAddresses());
    RestClient restClient = RestClient.builder()
        .requestFactory(ClientHttpRequestFactoryBuilder.jdk().build(settings))
        .build();

    assertThatThrownBy(() -> restClient.get()
        .uri("http://127.0.0.1:8080/supplier/prices/apple")
        .retrieve()
        .body(Price.class))
        .isInstanceOf(FilteredHostException.class)
        .satisfies(e -> System.out.println("SSRF filter -> " + e.getClass().getSimpleName() + ": " + e.getMessage()));
  }
}
