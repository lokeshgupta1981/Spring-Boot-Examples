package com.howtodoinjava.webfluxtest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class OrderStatusController {
  private final OrderStatusService service;

  public OrderStatusController(OrderStatusService service) {
    this.service = service;
  }

  @GetMapping("/orders/{orderId}/status")
  public Mono<ResponseEntity<OrderStatus>> status(@PathVariable String orderId) {
    return service.findStatus(orderId)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}
