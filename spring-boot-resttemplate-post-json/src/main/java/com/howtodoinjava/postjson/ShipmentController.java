package com.howtodoinjava.postjson;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class ShipmentController {
  private final AtomicLong nextId = new AtomicLong(104);

  @PostMapping("/shipments")
  public ResponseEntity<Shipment> create(@RequestBody CreateShipment request,
      @RequestHeader(value = "X-Request-ID", required = false) String requestId) {
    if (request.destination() == null || request.destination().isBlank() || request.weightKg() <= 0) {
      throw new ResponseStatusException(BAD_REQUEST, "Destination and positive weight are required");
    }
    Shipment shipment = new Shipment("S-" + nextId.getAndIncrement(),
        request.destination(), request.weightKg());
    var response = ResponseEntity.created(URI.create("/shipments/" + shipment.id()));
    if (requestId != null) {
      response.header("X-Request-ID", requestId);
    }
    return response.body(shipment);
  }

  public record CreateShipment(String destination, int weightKg) {}
  public record Shipment(String id, String destination, int weightKg) {}
}
