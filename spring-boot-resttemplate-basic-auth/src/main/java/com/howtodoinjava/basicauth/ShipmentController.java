package com.howtodoinjava.basicauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShipmentController {
  @GetMapping("/shipments/{id}/status")
  public ShipmentStatus status(@PathVariable String id) {
    return new ShipmentStatus(id, "READY_FOR_PICKUP");
  }

  public record ShipmentStatus(String id, String status) {}
}
