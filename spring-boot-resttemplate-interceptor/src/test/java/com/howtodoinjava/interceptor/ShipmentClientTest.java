package com.howtodoinjava.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@ExtendWith(OutputCaptureExtension.class)
class ShipmentClientTest {
  @Test
  void logsMetadataAndStillMapsResponse(CapturedOutput output) throws IOException {
    HttpServer server = shipmentServer();
    try {
      RestTemplate template = new RestTemplateBuilder()
          .baseUri("http://localhost:" + server.getAddress().getPort())
          .additionalInterceptors(new ExchangeLoggingInterceptor())
          .build();

      ShipmentStatus status = new ShipmentStatusClient(template).getStatus("S-42");

      assertThat(status).isEqualTo(new ShipmentStatus("S-42", "IN_TRANSIT"));
      assertThat(output).contains("HTTP GET http://localhost:", "/shipments/S-42 -> 200");
      assertThat(output).doesNotContain("IN_TRANSIT");
    } finally {
      server.stop(0);
    }
  }

  @Test
  void bufferingAllowsInterceptorToReadResponseBeforeMapping(CapturedOutput output) throws IOException {
    HttpServer server = shipmentServer();
    try {
      RestTemplate template = new RestTemplateBuilder()
          .baseUri("http://localhost:" + server.getAddress().getPort())
          .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
          .additionalInterceptors(new BodyLoggingInterceptor())
          .build();

      ShipmentStatus status = new ShipmentStatusClient(template).getStatus("S-42");

      assertThat(status).isEqualTo(new ShipmentStatus("S-42", "IN_TRANSIT"));
      assertThat(output).contains("Response body: {\"id\":\"S-42\",\"state\":\"IN_TRANSIT\"}");
    } finally {
      server.stop(0);
    }
  }

  private HttpServer shipmentServer() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
    server.createContext("/shipments/S-42", exchange -> {
      byte[] json = "{\"id\":\"S-42\",\"state\":\"IN_TRANSIT\"}".getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, json.length);
      try (var out = exchange.getResponseBody()) { out.write(json); }
    });
    server.start();
    return server;
  }
}
