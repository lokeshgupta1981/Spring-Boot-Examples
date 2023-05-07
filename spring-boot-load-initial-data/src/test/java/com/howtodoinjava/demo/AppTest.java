package com.howtodoinjava.demo;

import com.howtodoinjava.demo.entity.Item;
import com.howtodoinjava.demo.repository.ItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
/*@Import(TestDataSourceConfig.class)*/
public class AppTest {

  @Autowired
  ItemRepository itemRepository;

  @Test
  void testImportSqlRecords() {

    Optional<Item> item = itemRepository.findByName("Books");

    Assertions.assertNotNull(item.get().getId());
    Assertions.assertEquals("Books", item.get().getName());
  }

  @Test
  @Sql(scripts = {"classpath:data.sql"})
  void testDataSqlRecords() {

    Optional<Item> item = itemRepository.findByName("Laptops");

    Assertions.assertNotNull(item.get().getId());
    Assertions.assertEquals("Laptops", item.get().getName());
  }

  @Test
  @Sql
  void testMethodNameScript() {

    Optional<Item> item = itemRepository.findByName("Spices");

    Assertions.assertNotNull(item.get().getId());
    Assertions.assertEquals("Spices", item.get().getName());
  }
}
