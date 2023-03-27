package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.service.MongoRepositoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoRepositoryServiceImplTest {

  @Autowired
  private MongoRepositoryService mongoRepositoryService;

  Item item = null;
  Item itemToUpdate = null;

  @BeforeEach
  void setUp() {
    item = new Item();
    item.setId(1);
    item.setName("Whole Wheat Biscuit");
    item.setQuantity(5);
    item.setCategory("snacks");

    //Updated quantity
    itemToUpdate = new Item();
    itemToUpdate.setId(1);
    itemToUpdate.setName("Whole Wheat Biscuit");
    itemToUpdate.setQuantity(2);
    itemToUpdate.setCategory("snacks");
  }

  @AfterEach
  void tearDown() {
    item = null;
    itemToUpdate = null;
  }

  //TODO: rename all methods starting with test
  //TODO: Remove @Order annotation. Each test should be independent of other.
  //TODO: Each test will insert its test data and remove after assertions

  @Test
  @Order(1)
  void testAddGrocery() {
    Item savedItem = mongoRepositoryService.add(item);
    Assert.assertEquals(savedItem.getId(), item.getId());
    //TODO: assert name, category and quantity as well
  }

  @Test
  @Order(2)
  void testUpdateGrocery() {
    Item updatedItem = mongoRepositoryService.update(itemToUpdate);
    Assert.assertEquals(updatedItem.getQuantity(), itemToUpdate.getQuantity());
  }

  @Test
  @Order(3)
  void getAllItems() {
    List<Item> items = mongoRepositoryService.getAll();
    Assert.assertTrue(items.size() > 0);

    //TODO: save two items and count should be 2, then delete
  }

  @Test
  @Order(4)
  void getGroceryById() {
    Item item = mongoRepositoryService.getById(1);

    Assert.assertEquals(item.getName(), item.getName());
  }

  @Test
  @Order(5)
  void findItemByName() {
    Item itemFound = mongoRepositoryService.findByName(item.getName());

    Assert.assertEquals(item.getName(), itemFound.getName());
    Assert.assertEquals(item.getId(), itemFound.getId());
  }

  @Test
  @Order(6)
  void findAllByCategory() {
    List<Item> items =
        mongoRepositoryService.findAllByCategory(item.getCategory());
    Item item = items.get(0);
    Assert.assertNotNull(item.getName());
    Assert.assertNotNull(item.getQuantity());
    Assert.assertNull(item.getCategory());
  }

  @Test
  @Order(7)
  void findItemByQuantityBetween() {
    List<Item> items = mongoRepositoryService.findAllByQuantityBetween(1, 5);
    Assert.assertTrue(items.size() > 0);
  }

  @Test
  @Order(8)
  void deleteGrocery() {
    boolean response = mongoRepositoryService.delete(1);
    Assert.assertEquals(response, true);
  }


}