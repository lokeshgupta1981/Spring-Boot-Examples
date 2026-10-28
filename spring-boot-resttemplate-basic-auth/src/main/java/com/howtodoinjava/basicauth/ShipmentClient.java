package com.howtodoinjava.basicauth;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ShipmentClient {
  private final RestTemplate restTemplate;

  public ShipmentClient(RestTemplateBuilder builder, String username, String password) {
    this.restTemplate = builder.basicAuthentication(username, password).build();
  }

  public ShipmentController.ShipmentStatus getStatus(String baseUrl, String id) {
    return restTemplate.getForObject(baseUrl + "/shipments/{id}/status",
        ShipmentController.ShipmentStatus.class, id);
  }

  public static ShipmentController.ShipmentStatus getStatusWithCredentials(
      RestTemplate restTemplate, String baseUrl, String id, String username, String password) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(username, password);
    HttpEntity<Void> request = new HttpEntity<>(headers);
    ResponseEntity<ShipmentController.ShipmentStatus> response = restTemplate.exchange(
        baseUrl + "/shipments/{id}/status", HttpMethod.GET, request,
        ShipmentController.ShipmentStatus.class, id);
    return response.getBody();
  }
}
