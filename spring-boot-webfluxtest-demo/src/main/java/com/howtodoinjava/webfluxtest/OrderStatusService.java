package com.howtodoinjava.webfluxtest;

import reactor.core.publisher.Mono;

public interface OrderStatusService {
  Mono<OrderStatus> findStatus(String orderId);
}
