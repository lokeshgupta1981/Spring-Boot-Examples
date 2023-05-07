package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.service.MongoRepositoryService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
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

    Assertions.assertEquals(itemBiscuit.getId(), savedItem.getId());
    Assertions.assertEquals(itemBiscuit.getName(), savedItem.getName());
    Assertions.assertEquals(itemBiscuit.getQuantity(), savedItem.getQuantity());
    Assertions.assertEquals(itemBiscuit.getCategory(), savedItem.getCategory());

    mongoRepositoryService.delete(savedItem.getId());
  }

  @Test
  void testUpdateItem() {
    Item savedItem = mongoRepositoryService.add(itemBiscuit);

    itemToUpdate.setId(savedItem.getId());
    Item updatedItem = mongoRepositoryService.update(itemToUpdate);

    Assertions.assertEquals(updatedItem.getQuantity(), itemToUpdate.getQuantity());

    mongoRepositoryService.delete(updatedItem.getId());
  }

  @Test
  void testGetAllItems() {
    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    List<Item> items = mongoRepositoryService.getAll();

    Assertions.assertTrue(items.size() > 0);

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testGetAllItemsWithPagination() {
    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    List<Item> items = mongoRepositoryService.getAll(1, 2);
    Assertions.assertTrue(items.size() == 2);

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testGetItemById() {
    Item savedItem = mongoRepositoryService.add(itemBiscuit);

    Item fetchedItem = mongoRepositoryService.getById(savedItem.getId());

    Assertions.assertEquals(fetchedItem.getName(), savedItem.getName());

    mongoRepositoryService.delete(fetchedItem.getId());
  }

  @Test
  void testFindItemByName() {
    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    Item itemFound = mongoRepositoryService.findByName(itemBiscuit.getName());

    Assertions.assertEquals(itemBiscuit.getName(), itemFound.getName());
    Assertions.assertEquals(itemBiscuit.getId(), itemFound.getId());

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testFindAllByCategory() {

    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    List<Item> items = mongoRepositoryService.findAllByCategory(searchCategory);

    Assertions.assertTrue(items.size() == 2);

    Item item = items.get(0);
    Assertions.assertNotNull(item.getName());
    Assertions.assertNotNull(item.getQuantity());
    Assertions.assertNull(item.getCategory());

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testFindItemByQuantityBetween() {
    Item savedItemBiscuit = mongoRepositoryService.add(itemBiscuit);
    Item savedItemCrackers = mongoRepositoryService.add(itemCrackers);

    List<Item> items = mongoRepositoryService.findAllByQuantityBetween(1, 5);
    Assertions.assertTrue(items.size() == 1);

    mongoRepositoryService.delete(savedItemBiscuit.getId());
    mongoRepositoryService.delete(savedItemCrackers.getId());
  }

  @Test
  void testDeleteItem() {
    Item savedItem = mongoRepositoryService.add(itemBiscuit);

    boolean response = mongoRepositoryService.delete(savedItem.getId());
    Assertions.assertEquals(response, true);
  }
}