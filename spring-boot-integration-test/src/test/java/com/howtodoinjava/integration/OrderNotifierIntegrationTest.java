package com.howtodoinjava.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/orders.sql")
class OrderNotifierIntegrationTest {
  @LocalServerPort int port;
  @Autowired OrderRepository orders;
  @MockitoBean(enforceOverride = true) OrderNotifier notifier;

  @Test
  void keepsRealPersistenceAndMocksNotification() {
    var response = RestClient.create("http://localhost:" + port)
        .post().uri("/orders")
        .body(new OrderController.NewOrder("Marker"))
        .retrieve().toEntity(CustomerOrder.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Long id = response.getBody().getId();
    assertThat(orders.findById(id)).isPresent();
    verify(notifier).orderCreated(id);
  }
}
