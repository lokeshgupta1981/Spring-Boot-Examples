package com.howtodoinjava.basicauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShipmentClientTest {
  @LocalServerPort int port;
  @Autowired RestTemplateBuilder builder;

  private String baseUrl() {
    return "http://localhost:" + port;
  }

  @Test
  void fixedCredentialsReturnShipmentStatus() {
    var client = new ShipmentClient(builder, "warehouse-client", "demo-secret");
    var status = client.getStatus(baseUrl(), "S-104");
    assertEquals("S-104", status.id());
    assertEquals("READY_FOR_PICKUP", status.status());
  }

  @Test
  void requestCredentialsReturnShipmentStatus() {
    var status = ShipmentClient.getStatusWithCredentials(builder.build(),
        baseUrl(), "S-104", "warehouse-client", "demo-secret");
    assertEquals("READY_FOR_PICKUP", status.status());
  }

  @Test
  void wrongCredentialsReceiveUnauthorized() {
    var client = new ShipmentClient(builder, "warehouse-client", "wrong-secret");
    var error = assertThrows(HttpClientErrorException.Unauthorized.class,
        () -> client.getStatus(baseUrl(), "S-104"));
    assertEquals(401, error.getStatusCode().value());
  }

  @Test
  void missingCredentialsReceiveUnauthorized() {
    var error = assertThrows(HttpClientErrorException.Unauthorized.class,
        () -> builder.build().getForObject(baseUrl() + "/shipments/S-104/status", String.class));
    assertEquals(401, error.getStatusCode().value());
  }
}
