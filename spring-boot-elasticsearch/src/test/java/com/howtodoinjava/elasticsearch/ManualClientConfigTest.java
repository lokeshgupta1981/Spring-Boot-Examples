package com.howtodoinjava.elasticsearch;

import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.util.HexFormat;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/** ElasticsearchConfiguration subclass against a secured node (HTTPS, password, CA fingerprint). */
@SpringBootTest(properties = "cars.demo.enabled=false")
@ActiveProfiles("manual-client")
@Testcontainers
class ManualClientConfigTest {

  @Container
  static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(ElasticsearchTestConfig.IMAGE)
      .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
      .withEnv("cluster.routing.allocation.disk.threshold_enabled", "false")
      .waitingFor(ElasticsearchTestConfig.securedNodeReady());

  @DynamicPropertySource
  static void elasticsearchProperties(DynamicPropertyRegistry registry) throws Exception {
    byte[] pem = elasticsearch.caCertAsBytes().orElseThrow();
    X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
        .generateCertificate(new ByteArrayInputStream(pem));
    String fingerprint = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(cert.getEncoded()));

    registry.add("cars.es.host", elasticsearch::getHttpHostAddress);
    registry.add("cars.es.ca-fingerprint", () -> fingerprint);
    registry.add("cars.es.password", () -> "changeme");
  }

  @Autowired
  CarListingRepository repository;

  @Autowired
  ElasticsearchClient client;

  @Test
  void customClientConfigurationIsUsed() throws Exception {
    repository.saveAll(CarData.listings());
    assertThat(repository.count()).isEqualTo(8);
    String version = client.info().version().number();
    System.out.println("RESULT server version " + version);
    assertThat(ElasticsearchTestConfig.IMAGE).endsWith(":" + version);
  }
}
