package com.howtodoinjava.demo;

import com.howtodoinjava.demo.ServiceConnectionBeanTest.TestContainersConfiguration;
import com.howtodoinjava.demo.dao.ItemRepository;
import com.howtodoinjava.demo.dao.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Import(TestContainersConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceConnectionBeanTest {

  @Autowired
  ItemRepository itemRepository;

  @Autowired
  PostgreSQLContainer postgres;

  @BeforeAll
  void setup() {
    postgres.start();
  }

  @Test
  void testConnection() {
    Assertions.assertTrue(postgres.isRunning());
    Assertions.assertEquals("testUser", postgres.getUsername());
    Assertions.assertEquals("testSecret", postgres.getPassword());
    Assertions.assertEquals("testDatabase", postgres.getDatabaseName());
  }

  @Test
  void testSaveItem() {
    Item item = new Item(null, "Grocery");
    Item savedItem = itemRepository.save(item);

    Assertions.assertNotNull(savedItem.getId());
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
