package com.howtodoinjava.timeouts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.SocketTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.ResourceAccessException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShipmentClientTest {
  @LocalServerPort int port;
  @Autowired RestTemplateBuilder builder;

  @Test
  void immediateResponseSucceeds() {
    var client = new ShipmentClient(builder);
    var status = client.status("http://localhost:" + port, 0);
    assertEquals("READY_FOR_PICKUP", status.status());
  }

  @Test
  void delayedResponseExceedsReadTimeout() {
    var client = new ShipmentClient(builder);
    var error = assertThrows(ResourceAccessException.class,
        () -> client.status("http://localhost:" + port, 3000));
    assertInstanceOf(SocketTimeoutException.class, error.getMostSpecificCause());
  }
}
