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

  Item itemBiscuit = null;
  Item itemCrackers = null;
  Item itemToUpdate = null;
  String searchCategory = "snacks";

  @BeforeEach
  void setUp() {
    itemBiscuit = new Item();
    itemBiscuit.setName("Whole Wheat Biscuit");
    itemBiscuit.setQuantity(4);
    itemBiscuit.setCategory("snacks");

    itemCrackers = new Item();
    itemCrackers.setName("Cheese Crackers");
    itemCrackers.setQuantity(8);
    itemCrackers.setCategory("snacks");

    //Updated quantity
    itemToUpdate = new Item();
    itemToUpdate.setName("Whole Wheat Biscuit");
    itemToUpdate.setQuantity(2);
    itemToUpdate.setCategory("snacks");
  }

  @AfterEach
  void tearDown() {
    itemBiscuit = null;
    itemToUpdate = null;
    itemCrackers = null;
  }

  @Test
  void testAddItem() {
    Item savedItem = mongoRepositoryService.add(itemBiscuit);

    Assert.assertEquals(itemBiscuit.getId(), savedItem.getId());
    Assert.assertEquals(itemBiscuit.getName(), savedItem.getName());
    Assert.assertEquals(itemBiscuit.getQuantity(), savedItem.getQuantity());
    Assert.assertEquals(itemBiscuit.getCategory(), savedItem.getCategory());

    mongoRepositoryService.delete(savedItem.getId());
  }

  @Test
  void testUpdateItem() {
    Item savedItem = mongoRepositoryService.add(itemBiscuit);

    itemToUpdate.setId(savedItem.getId());
    Item updatedItem = mongoRepositoryService.update(itemToUpdate);

    Assert.assertEquals(updatedItem.getQuantity(), itemToUpdate.getQuantity());

    mongoRepositoryService.delete(updatedItem.getId());
  }

  @Test
  void testGetAllItems() {
    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    List<Item> items = mongoRepositoryService.getAll();

    Assert.assertTrue(items.size() > 0);

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testGetAllItemsWithPagination() {
    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    List<Item> items = mongoRepositoryService.getAll(1, 2);
    Assert.assertTrue(items.size() == 2);

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testGetItemById() {
    Item savedItem = mongoRepositoryService.add(itemBiscuit);

    Item fetchedItem = mongoRepositoryService.getById(savedItem.getId());

    Assert.assertEquals(fetchedItem.getName(), savedItem.getName());

    mongoRepositoryService.delete(fetchedItem.getId());
  }

  @Test
  void testFindItemByName() {
    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    Item itemFound = mongoRepositoryService.findByName(itemBiscuit.getName());

    Assert.assertEquals(itemBiscuit.getName(), itemFound.getName());
    Assert.assertEquals(itemBiscuit.getId(), itemFound.getId());

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testFindAllByCategory() {

    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    List<Item> items = mongoRepositoryService.findAllByCategory(searchCategory);

    Assert.assertTrue(items.size() == 2);

    Item item = items.get(0);
    Assert.assertNotNull(item.getName());
    Assert.assertNotNull(item.getQuantity());
    Assert.assertNull(item.getCategory());

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testFindItemByQuantityBetween() {
    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    List<Item> items = mongoRepositoryService.findAllByQuantityBetween(1, 5);
    Assert.assertTrue(items.size() == 1);

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testDeleteItem() {
    Item savedItem = mongoRepositoryService.add(itemBiscuit);

    boolean response = mongoRepositoryService.delete(savedItem.getId());
    Assert.assertEquals(response, true);
  }
}