package com.howtodoinjava.controllertest;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {
  private final DeliveryService service;

  public DeliveryController(DeliveryService service) { this.service = service; }

  @GetMapping("/{trackingId}")
  public ResponseEntity<DeliveryStatus> find(@PathVariable String trackingId) {
    return ResponseEntity.of(service.find(trackingId));
  }

  @PostMapping
  public ResponseEntity<DeliveryStatus> create(@RequestBody NewDelivery request) {
    DeliveryStatus saved = service.create(request.address());
    return ResponseEntity.created(URI.create("/deliveries/" + saved.trackingId()))
        .body(saved);
  }

  public record NewDelivery(String address) {}
}
