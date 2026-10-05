package com.howtodoinjava.library.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.service.registry.HttpServiceProxyRegistry;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockClientTest {

  static HttpServer stockServer;

  @DynamicPropertySource
  static void stockUrl(DynamicPropertyRegistry registry) throws IOException {
    stockServer = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
    stockServer.createContext("/stock/", exchange -> {
      String title = exchange.getRequestURI().getPath().substring("/stock/".length());
      byte[] body = (title.equals("dune") ? "3" : "0").getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().add("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream out = exchange.getResponseBody()) {
        out.write(body);
      }
    });
    stockServer.start();
    registry.add("stock.base-url", () -> "http://localhost:" + stockServer.getAddress().getPort());
  }

  @AfterAll
  static void stop() {
    stockServer.stop(0);
  }

  @Autowired
  StockClient stockClient;

  @Autowired
  HttpServiceProxyRegistry registry;

  @Test
  void stockClientIsABeanFromTheGroup() {
    int copies = stockClient.copies("dune");          // 3
    System.out.println("stockClient.copies(\"dune\") = " + copies);
    System.out.println("groups = " + registry.getGroupNames());
    assertThat(copies).isEqualTo(3);
    assertThat(registry.getGroupNames()).contains("stock");
    assertThat(registry.getClient("stock", StockClient.class)).isSameAs(stockClient);
  }
}
