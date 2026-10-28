package com.howtodoinjava.postjson;

import java.net.URI;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ShipmentClient {
  private final RestTemplate restTemplate;

  public ShipmentClient(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  public ResponseEntity<ShipmentController.Shipment> create(String baseUrl,
      ShipmentController.CreateShipment request) {
    return restTemplate.postForEntity(baseUrl + "/shipments", request,
        ShipmentController.Shipment.class);
  }

  public ResponseEntity<ShipmentController.Shipment> createWithHeaders(String baseUrl,
      ShipmentController.CreateShipment request, String requestId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Request-ID", requestId);
    HttpEntity<ShipmentController.CreateShipment> entity = new HttpEntity<>(request, headers);
    return restTemplate.postForEntity(baseUrl + "/shipments", entity,
        ShipmentController.Shipment.class);
  }

  public ShipmentController.Shipment createForBody(String baseUrl,
      ShipmentController.CreateShipment request) {
    return restTemplate.postForObject(baseUrl + "/shipments", request,
        ShipmentController.Shipment.class);
  }

  public URI createForLocation(String baseUrl, ShipmentController.CreateShipment request) {
    return restTemplate.postForLocation(baseUrl + "/shipments", request);
  }
}
