package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.service.MongoRepositoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.util.List;

@SpringBootTest
class MongoRepositoryServiceImplTest {

  @Autowired
  private MongoRepositoryService mongoRepositoryService;

  Item item1 = null;
  Item item2 = null;
  Item item1ToUpdate = null;

  String searchCategory = "snacks";

  @BeforeEach
  void setUp() {
    item1 = new Item();
    item1.setId(1);
    item1.setName("Whole Wheat Biscuit");
    item1.setQuantity(4);
    item1.setCategory("snacks");

    item2 = new Item();
    item2.setId(2);
    item2.setName("Cheese Crackers");
    item2.setQuantity(8);
    item2.setCategory("snacks");

    //Updated quantity
    item1ToUpdate = new Item();
    item1ToUpdate.setId(1);
    item1ToUpdate.setName("Whole Wheat Biscuit");
    item1ToUpdate.setQuantity(2);
    item1ToUpdate.setCategory("snacks");
  }

  @AfterEach
  void tearDown() {
    item1 = null;
    item1ToUpdate = null;
    item2 = null;
  }

  @Test
  void testAddItem() {
    Item savedItem = mongoRepositoryService.add(item1);
    Assert.assertEquals(savedItem.getId(), item1.getId());
    Assert.assertEquals(savedItem.getName(), item1.getName());
    Assert.assertEquals(savedItem.getQuantity(), item1.getQuantity());
    Assert.assertEquals(savedItem.getCategory(), item1.getCategory());

    mongoRepositoryService.delete(savedItem.getId());
  }

  @Test
  void testUpdateGrocery() {
    mongoRepositoryService.add(item1);

    Item updatedItem = mongoRepositoryService.update(item1ToUpdate);

    Assert.assertEquals(updatedItem.getQuantity(), item1ToUpdate.getQuantity());
    mongoRepositoryService.delete(updatedItem.getId());
  }

  @Test
  void testGetAllItems() {
    mongoRepositoryService.add(item1);
    mongoRepositoryService.add(item2);
    List<Item> items = mongoRepositoryService.getAll();
    Assert.assertTrue(items.size() == 2);

    mongoRepositoryService.delete(item1.getId());
    mongoRepositoryService.delete(item2.getId());
  }

  @Test
  void testGetAllItemsWithPagination() {
    mongoRepositoryService.add(item1);
    mongoRepositoryService.add(item2);

    List<Item> items = mongoRepositoryService.getAll();
    Assert.assertTrue(items.size() == 2);

    mongoRepositoryService.delete(item1.getId());
    mongoRepositoryService.delete(item2.getId());
  }

  @Test
  void testGetItemById() {
    mongoRepositoryService.add(item1);

    Item item = mongoRepositoryService.getById(1);

    Assert.assertEquals(item.getName(), item.getName());

    mongoRepositoryService.delete(item1.getId());
  }

  @Test
  void testFindItemByName() {
    mongoRepositoryService.add(item1);
    mongoRepositoryService.add(item2);

    Item itemFound = mongoRepositoryService.findByName(item1.getName());

    Assert.assertEquals(item1.getName(), itemFound.getName());
    Assert.assertEquals(item1.getId(), itemFound.getId());

    mongoRepositoryService.delete(item1.getId());
    mongoRepositoryService.delete(item2.getId());
  }

  @Test
  void testFindAllByCategory() {

    mongoRepositoryService.add(item1);
    mongoRepositoryService.add(item2);

    List<Item> items =
        mongoRepositoryService.findAllByCategory(searchCategory);


    Assert.assertTrue(items.size() == 2);

    Item item = items.get(0);
    Assert.assertNotNull(item.getName());
    Assert.assertNotNull(item.getQuantity());
    Assert.assertNull(item.getCategory());

    mongoRepositoryService.delete(item1.getId());
    mongoRepositoryService.delete(item2.getId());
  }

  @Test
  void testFindItemByQuantityBetween() {
    mongoRepositoryService.add(item1);
    mongoRepositoryService.add(item2);

    List<Item> items = mongoRepositoryService.findAllByQuantityBetween(1, 5);
    Assert.assertTrue(items.size() == 1);

    mongoRepositoryService.delete(item1.getId());
    mongoRepositoryService.delete(item2.getId());
  }

  @Test
  void testDeleteItem() {
    mongoRepositoryService.add(item1);


    boolean response = mongoRepositoryService.delete(1);
    Assert.assertEquals(response, true);
  }


}