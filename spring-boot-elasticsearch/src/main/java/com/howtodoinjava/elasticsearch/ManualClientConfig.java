package com.howtodoinjava.elasticsearch;

import java.time.Duration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * Only needed when the spring.elasticsearch.* properties are not enough. Active with the
 * "manual-client" profile; Spring Boot's client auto-configuration then backs off.
 */
@Configuration
@Profile("manual-client")
public class ManualClientConfig extends ElasticsearchConfiguration {

  private final Environment env;

  public ManualClientConfig(Environment env) {
    this.env = env;
  }

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(env.getRequiredProperty("cars.es.host"))           // "localhost:9200"
        .usingSsl(env.getRequiredProperty("cars.es.ca-fingerprint"))    // SHA-256 of the CA certificate
        .withBasicAuth("elastic", env.getRequiredProperty("cars.es.password"))
        .withConnectTimeout(Duration.ofSeconds(5))
        .withSocketTimeout(Duration.ofSeconds(30))
        .build();
  }
}
