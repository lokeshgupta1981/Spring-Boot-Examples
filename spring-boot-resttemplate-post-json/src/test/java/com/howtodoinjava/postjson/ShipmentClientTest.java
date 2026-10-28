package com.howtodoinjava.postjson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShipmentClientTest {
  @LocalServerPort int port;
  @Autowired RestTemplateBuilder builder;

  private String baseUrl() { return "http://localhost:" + port; }

  @Test
  void postForEntityReturnsCreatedStatusBodyAndLocation() {
    var response = new ShipmentClient(builder).create(baseUrl(),
        new ShipmentController.CreateShipment("Delhi", 3));
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Delhi", response.getBody().destination());
    assertEquals("/shipments/" + response.getBody().id(), response.getHeaders().getLocation().toString());
  }

  @Test
  void explicitHeadersAndUnicodeBodyReachServer() {
    var response = new ShipmentClient(builder).createWithHeaders(baseUrl(),
        new ShipmentController.CreateShipment("Bengaluru – दक्षिण", 2), "req-42");
    assertEquals("Bengaluru – दक्षिण", response.getBody().destination());
    assertEquals("req-42", response.getHeaders().getFirst("X-Request-ID"));
  }

  @Test
  void postForObjectReturnsOnlyMappedBody() {
    var shipment = new ShipmentClient(builder).createForBody(baseUrl(),
        new ShipmentController.CreateShipment("Mumbai", 1));
    assertNotNull(shipment.id());
  }

  @Test
  void postForLocationReturnsLocationHeader() {
    var location = new ShipmentClient(builder).createForLocation(baseUrl(),
        new ShipmentController.CreateShipment("Pune", 4));
    assertTrue(location.toString().startsWith("/shipments/S-"));
  }

  @Test
  void invalidBodyProducesClientErrorException() {
    var error = assertThrows(HttpClientErrorException.BadRequest.class,
        () -> new ShipmentClient(builder).create(baseUrl(),
            new ShipmentController.CreateShipment("", 0)));
    assertEquals(400, error.getStatusCode().value());
  }
}
