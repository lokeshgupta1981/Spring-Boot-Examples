package com.howtodoinjava.webfluxtest;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class InMemoryOrderStatusService implements OrderStatusService {
  @Override
  public Mono<OrderStatus> findStatus(String orderId) {
    return "A-17".equals(orderId)
        ? Mono.just(new OrderStatus(orderId, "SHIPPED"))
        : Mono.empty();
  }
}
