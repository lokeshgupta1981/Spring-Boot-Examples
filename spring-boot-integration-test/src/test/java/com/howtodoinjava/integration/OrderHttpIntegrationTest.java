package com.howtodoinjava.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/orders.sql")
class OrderHttpIntegrationTest {
  @LocalServerPort int port;
  @Autowired OrderRepository orders;

  private RestClient client() { return RestClient.create("http://localhost:" + port); }

  @Test
  void readsSeededOrderThroughHttpAndJpa() {
    CustomerOrder response = client().get().uri("/orders/17")
        .retrieve().body(CustomerOrder.class);

    assertThat(response.getItem()).isEqualTo("Notebook");
    assertThat(response.getStatus()).isEqualTo("PACKED");
    assertThat(orders.findById(17L)).isPresent();
  }

  @Test
  void returns404ForUnknownOrder() {
    var status = client().get().uri("/orders/999")
        .exchange((request, response) -> response.getStatusCode());
    assertThat(status).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void postPersistsOrderInServerTransaction() {
    ResponseEntity<CustomerOrder> response = client().post().uri("/orders")
        .body(new OrderController.NewOrder("Pencil"))
        .retrieve().toEntity(CustomerOrder.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Long id = response.getBody().getId();
    assertThat(orders.findById(id)).get().extracting(CustomerOrder::getItem)
        .isEqualTo("Pencil");
    assertThat(response.getHeaders().getLocation().toString()).isEqualTo("/orders/" + id);
  }
}
