package com.howtodoinjava.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class AppTest {

  public static void main(String[] args) {
    SpringApplication.from(App::main).with(TestContainersConfiguration.class).run(args);
  }

  @TestConfiguration(proxyBeanMethods = false)
  public static class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    @RestartScope
    public PostgreSQLContainer<?> postgreSQLContainer() {
      return new PostgreSQLContainer(DockerImageName.parse("postgres:15.1"))
          .withUsername("testUser")
          .withPassword("testSecret")
          .withDatabaseName("testDatabase");
    }
  }
}
