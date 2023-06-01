package com.howtodoinjava.demo;

import com.howtodoinjava.demo.dao.ItemRepository;
import com.howtodoinjava.demo.dao.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
public class ServiceConnectionFieldTest {

  @Autowired
  ItemRepository itemRepository;

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:15.1"))
      .withUsername("testUser")
      .withPassword("testSecret")
      .withDatabaseName("testDatabase");

  /*@DynamicPropertySource
  static void datasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }*/

  @BeforeAll
  static void setup(){
    postgres.start();
  }

  @Test
  void testConnection(){
    Assertions.assertTrue(postgres.isRunning());
    Assertions.assertEquals("testUser", postgres.getUsername());
    Assertions.assertEquals("testSecret", postgres.getPassword());
    Assertions.assertEquals("testDatabase", postgres.getDatabaseName());
  }

  @Test
  void testSaveItem(){
    Item item = new Item(null, "Grocery");
    Item savedItem = itemRepository.save(item);

    Assertions.assertNotNull(savedItem.getId());
  }
}
