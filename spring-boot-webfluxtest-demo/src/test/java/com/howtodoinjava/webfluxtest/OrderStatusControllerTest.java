package com.howtodoinjava.webfluxtest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(OrderStatusController.class)
class OrderStatusControllerTest {
  @Autowired WebTestClient client;
  @MockitoBean OrderStatusService service;

  @Test
  void returnsOrderStatusAsJson() {
    when(service.findStatus("A-17"))
        .thenReturn(Mono.just(new OrderStatus("A-17", "SHIPPED")));

    client.get().uri("/orders/A-17/status").exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.orderId").isEqualTo("A-17")
        .jsonPath("$.state").isEqualTo("SHIPPED");

    verify(service).findStatus("A-17");
  }

  @Test
  void returnsNotFoundWhenServiceIsEmpty() {
    when(service.findStatus("missing")).thenReturn(Mono.empty());

    client.get().uri("/orders/missing/status").exchange()
        .expectStatus().isNotFound()
        .expectBody().isEmpty();

    verify(service).findStatus("missing");
  }
}
