package com.howtodoinjava.httpclient;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

class ApacheClientConfigurationTest {
  @Test
  void apacheFactoryExecutesRequestAndMapsInventoryJson() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
    server.createContext("/inventory/SKU-42", exchange -> {
      byte[] json = "{\"sku\":\"SKU-42\",\"available\":true}".getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, json.length);
      try (var out = exchange.getResponseBody()) { out.write(json); }
    });
    server.start();
    ApacheClientConfiguration configuration = new ApacheClientConfiguration();
    var pool = configuration.connectionManager();
    try (var httpClient = configuration.apacheHttpClient(pool)) {
      var template = configuration.inventoryRestTemplate(new RestTemplateBuilder(), httpClient,
          "http://localhost:" + server.getAddress().getPort());
      assertThat(template.getRequestFactory()).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
      assertThat(pool.getMaxTotal()).isEqualTo(20);
      assertThat(new InventoryClient(template).availability("SKU-42"))
          .isEqualTo(new InventoryAvailability("SKU-42", true));
    } finally {
      server.stop(0);
    }
  }
}
