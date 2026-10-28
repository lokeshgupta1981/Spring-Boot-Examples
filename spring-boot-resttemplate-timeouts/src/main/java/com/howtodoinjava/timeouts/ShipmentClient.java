package com.howtodoinjava.timeouts;

import java.time.Duration;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ShipmentClient {
  private final RestTemplate restTemplate;

  public ShipmentClient(RestTemplateBuilder builder) {
    this.restTemplate = builder
        .requestFactory(SimpleClientHttpRequestFactory.class)
        .connectTimeout(Duration.ofSeconds(2))
        .readTimeout(Duration.ofSeconds(1))
        .build();
  }

  public ShipmentController.ShipmentStatus status(String baseUrl, long delayMs) {
    return restTemplate.getForObject(baseUrl + "/shipments/status?delayMs={delayMs}",
        ShipmentController.ShipmentStatus.class, delayMs);
  }
}
