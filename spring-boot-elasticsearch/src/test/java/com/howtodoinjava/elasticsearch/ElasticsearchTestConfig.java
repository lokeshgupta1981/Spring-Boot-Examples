package com.howtodoinjava.elasticsearch;

import java.time.Duration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

/** One Elasticsearch 9.4.5 node with security turned off, shared by all tests that import it. */
@TestConfiguration(proxyBeanMethods = false)
public class ElasticsearchTestConfig {

  public static final String IMAGE =
      System.getProperty("es.image", "docker.elastic.co/elasticsearch/elasticsearch:9.4.5");

  /**
   * With security on, the elastic user can get HTTP 401 for a short time after the "started" log line.
   * This waits until the password is accepted.
   */
  public static WaitStrategy securedNodeReady() {
    return Wait.forHttps("/_security/_authenticate")
        .forPort(9200)
        .allowInsecure()
        .withBasicCredentials("elastic", "changeme")
        .forStatusCode(200)
        .withStartupTimeout(Duration.ofMinutes(4));
  }

  @Bean
  @ServiceConnection
  ElasticsearchContainer elasticsearchContainer() {
    return new ElasticsearchContainer(IMAGE)
        .withEnv("xpack.security.enabled", "false")
        .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
        .withEnv("cluster.routing.allocation.disk.threshold_enabled", "false")
        .withStartupTimeout(Duration.ofMinutes(4));
  }
}
