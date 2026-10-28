package com.howtodoinjava.interceptor;

import org.springframework.web.client.RestTemplate;

public final class ShipmentStatusClient {
  private final RestTemplate restTemplate;

  public ShipmentStatusClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ShipmentStatus getStatus(String id) {
    return restTemplate.getForObject("/shipments/{id}", ShipmentStatus.class, id);
  }
}
