package com.howtodoinjava.k8s;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.InetAddress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Runs the API against PostgreSQL 18.6 in a container. The two properties mimic
 * the values that the ConfigMap and the Secret pass to the pods in k8s/.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {
    "greeting.message=Hello from Kubernetes",
    "greeting.api-key=k8s-demo-key"})
class GreetingApiTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.6");

  @Autowired
  MockMvc mvc;

  @Autowired
  JdbcClient jdbc;

  @BeforeEach
  void clearVisits() {
    jdbc.sql("delete from visit").update();
  }

  @Test
  void returnsMessageFromConfigAndCountsVisits() throws Exception {
    String host = InetAddress.getLocalHost().getHostName();

    mvc.perform(get("/api/greeting").header("X-API-Key", "k8s-demo-key"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Hello from Kubernetes"))
        .andExpect(jsonPath("$.visits").value(1))
        .andExpect(jsonPath("$.pod").value(host))
        .andExpect(jsonPath("$.version").value("1.0.0"));

    mvc.perform(get("/api/greeting").header("X-API-Key", "k8s-demo-key"))
        .andExpect(jsonPath("$.visits").value(2));
  }

  @Test
  void rejectsMissingOrWrongApiKey() throws Exception {
    mvc.perform(get("/api/greeting"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("missing or wrong X-API-Key"));

    mvc.perform(get("/api/greeting").header("X-API-Key", "wrong"))
        .andExpect(status().isUnauthorized());

    long rows = jdbc.sql("select count(*) from visit").query(Long.class).single();
    assertThat(rows).isZero();
  }

  @Test
  void exposesLivenessAndReadinessProbes() throws Exception {
    mvc.perform(get("/actuator/health/liveness"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
    mvc.perform(get("/actuator/health/readiness"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }
}
