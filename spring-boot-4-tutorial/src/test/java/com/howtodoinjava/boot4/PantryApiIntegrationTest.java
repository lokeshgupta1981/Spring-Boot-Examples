package com.howtodoinjava.boot4;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = "management.tracing.export.enabled=false")
@AutoConfigureRestTestClient
class PantryApiIntegrationTest {

  @Autowired
  RestTestClient client;

  @Test
  void versionTwoOverRealHttp() {
    client.get().uri("/pantry/banana")
        .header("API-Version", "2")
        .exchange()
        .expectStatus().isOk()
        .expectBody(PantryItem.class)
        .value(item -> assertThat(item.quantity()).isEqualTo(3));
  }

  @Test
  void requestsRunOnVirtualThreads() {
    String thread = client.get().uri("/pantry/thread")
        .exchange()
        .expectStatus().isOk()
        .returnResult(String.class)
        .getResponseBody();
    System.out.println("Handled by: " + thread);
    assertThat(thread).startsWith("VirtualThread[");
  }
}
