package com.howtodoinjava.integration;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderRepository orders;
  private final OrderNotifier notifier;

  public OrderController(OrderRepository orders, OrderNotifier notifier) {
    this.orders = orders;
    this.notifier = notifier;
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerOrder> find(@PathVariable Long id) {
    return orders.findById(id).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<CustomerOrder> create(@RequestBody NewOrder request) {
    CustomerOrder saved = orders.save(new CustomerOrder(request.item(), "NEW"));
    notifier.orderCreated(saved.getId());
    return ResponseEntity.created(URI.create("/orders/" + saved.getId())).body(saved);
  }

  public record NewOrder(String item) {}
}
