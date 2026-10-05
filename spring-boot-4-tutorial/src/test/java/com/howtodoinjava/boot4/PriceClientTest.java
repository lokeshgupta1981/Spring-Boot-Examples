package com.howtodoinjava.boot4;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/** Calls a stub supplier API that runs on a random port. */
@SpringBootTest(webEnvironment = WebEnvironment.NONE,
    properties = "management.tracing.export.enabled=false")
class PriceClientTest {

  static final AtomicInteger bananaCalls = new AtomicInteger();
  static final HttpServer supplier = startSupplier();

  @DynamicPropertySource
  static void supplierUrl(DynamicPropertyRegistry registry) {
    registry.add("spring.http.serviceclient.prices.base-url",
        () -> "http://localhost:" + supplier.getAddress().getPort());
  }

  @AfterAll
  static void stop() {
    supplier.stop(0);
  }

  @Autowired
  PriceClient priceClient;

  @Autowired
  PriceService priceService;

  @Test
  void httpServiceClientCallsTheSupplier() {
    Price price = priceClient.price("apple");
    System.out.println("PriceClient returned: " + price);
    assertThat(price).isEqualTo(new Price("apple", 120));
  }

  @Test
  void retryableRetriesAfterServerError() {
    Price price = priceService.priceOf("banana");
    System.out.println("Supplier calls for banana: " + bananaCalls.get() + ", result: " + price);
    assertThat(price.cents()).isEqualTo(45);
    assertThat(bananaCalls.get()).isEqualTo(2);
  }

  private static HttpServer startSupplier() {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
      server.createContext("/prices/apple", exchange -> reply(exchange, 200,
          "{\"item\":\"apple\",\"cents\":120}"));
      // banana fails once with 503, then succeeds
      server.createContext("/prices/banana", exchange -> {
        if (bananaCalls.incrementAndGet() == 1) {
          reply(exchange, 503, "{}");
        } else {
          reply(exchange, 200, "{\"item\":\"banana\",\"cents\":45}");
        }
      });
      server.start();
      return server;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private static void reply(com.sun.net.httpserver.HttpExchange exchange, int status, String json)
      throws IOException {
    byte[] body = json.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(status, body.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(body);
    }
  }
}
