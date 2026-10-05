package com.howtodoinjava.elasticsearch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.testcontainers.service.connection.Ssl;
import org.springframework.context.annotation.Bean;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import static org.assertj.core.api.Assertions.assertThat;

/** Security on (the default since Elasticsearch 8): HTTPS, the elastic user and the container's CA certificate. */
@SpringBootTest(properties = "cars.demo.enabled=false")
class SecuredClusterTest {

  @TestConfiguration(proxyBeanMethods = false)
  static class SecuredElasticsearch {

    @Bean
    @Ssl
    @ServiceConnection
    ElasticsearchContainer securedElasticsearch() {
      return new ElasticsearchContainer(ElasticsearchTestConfig.IMAGE)
          .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
        .withEnv("cluster.routing.allocation.disk.threshold_enabled", "false")
        .waitingFor(ElasticsearchTestConfig.securedNodeReady());
    }
  }

  @Autowired
  CarListingRepository repository;

  @Test
  void connectsOverHttpsWithPassword() {
    repository.saveAll(CarData.listings());
    assertThat(repository.count()).isEqualTo(8);
    assertThat(repository.findByMake("Tesla")).hasSize(1);
  }
}
