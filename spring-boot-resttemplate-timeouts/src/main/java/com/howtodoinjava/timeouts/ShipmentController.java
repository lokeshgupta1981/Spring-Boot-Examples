package com.howtodoinjava.timeouts;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShipmentController {
  @GetMapping("/shipments/status")
  public ShipmentStatus status(@RequestParam(defaultValue = "0") long delayMs)
      throws InterruptedException {
    if (delayMs > 0) {
      Thread.sleep(delayMs);
    }
    return new ShipmentStatus("S-104", "READY_FOR_PICKUP");
  }

  public record ShipmentStatus(String id, String status) {}
}
