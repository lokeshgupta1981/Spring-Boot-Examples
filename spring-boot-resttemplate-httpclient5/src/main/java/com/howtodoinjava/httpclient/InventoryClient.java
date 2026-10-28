package com.howtodoinjava.httpclient;

import org.springframework.web.client.RestTemplate;

public final class InventoryClient {
  private final RestTemplate restTemplate;

  public InventoryClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public InventoryAvailability availability(String sku) {
    return restTemplate.getForObject("/inventory/{sku}", InventoryAvailability.class, sku);
  }
}
