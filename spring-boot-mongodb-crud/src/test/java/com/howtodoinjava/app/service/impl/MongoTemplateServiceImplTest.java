package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.service.MongoTemplateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Disabled
class MongoTemplateServiceImplTest {

  @Autowired
  MongoTemplateService mongoTemplateService;

  Integer pageNumber = 1;
  Integer pageSize = 5;
  Item itemSpices = null;
  Item itemSnacks = null;
  Item itemToUpdate = null;
  String searchCategory = "spices";

  @BeforeEach
  void setUp() {
    itemSpices = new Item();
    itemSpices.setName("Dried Red Chilli");
    itemSpices.setQuantity(3);
    itemSpices.setCategory("spices");

    itemToUpdate = new Item();
    itemToUpdate.setQuantity(1);
    itemToUpdate.setCategory("spices");
    itemToUpdate.setName("Dried Red Chilli");

    itemSnacks = new Item();
    itemSnacks.setName("Cheese Crackers");
    itemSnacks.setQuantity(8);
    itemSnacks.setCategory("snacks");

  }

  @AfterEach
  void tearDown() {
    itemSpices = null;
    itemToUpdate = null;
    itemSnacks = null;
  }


  @Test
  void testAddItem() {

    Item savedItem = mongoTemplateService.add(itemSpices);

    Assertions.assertEquals(savedItem.getId(), itemSpices.getId());
    Assertions.assertEquals(savedItem.getName(), itemSpices.getName());
    Assertions.assertEquals(savedItem.getQuantity(), itemSpices.getQuantity());
    Assertions.assertEquals(savedItem.getCategory(), itemSpices.getCategory());

    mongoTemplateService.delete(savedItem.getId());
  }

  @Test
  void testUpdateItem() {
    Item savedItem = mongoTemplateService.add(itemSpices);

    itemToUpdate.setId(savedItem.getId());
    Item updatedItem = mongoTemplateService.update(itemToUpdate);

    Assertions.assertEquals(updatedItem.getQuantity(),
        itemToUpdate.getQuantity());

    mongoTemplateService.delete(updatedItem.getId());
  }

  @Test
  void testGetAllItems() {
    Item savedItemSpices = mongoTemplateService.add(itemSpices);
    Item savedItemSnacks = mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.getAll();
    Assertions.assertTrue(items.size() == 2);

    mongoTemplateService.delete(savedItemSpices.getId());
    mongoTemplateService.delete(savedItemSnacks.getId());

  }

  @Test
  void testGetItemById() {
    Item savedItem = mongoTemplateService.add(itemSpices);

    Item item = mongoTemplateService.getById(savedItem.getId());

    Assertions.assertEquals(item.getName(), item.getName());

    mongoTemplateService.delete(savedItem.getId());

  }

  @Test
  void testSearchItemWithName() {

    Item savedItemSpices = mongoTemplateService.add(itemSpices);
    Item savedItemSnacks = mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.search("Dried Red " +
            "Chilli", null,
        null, null);

    Assertions.assertTrue(items.size() > 0);

    mongoTemplateService.delete(savedItemSpices.getId());
    mongoTemplateService.delete(savedItemSnacks.getId());
  }

  @Test
  void testSearchItemWithQuantity() {
    Item savedItemSpices = mongoTemplateService.add(itemSpices);
    Item savedItemSnacks = mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.search(null, 1,
        5, null);

    Assertions.assertTrue(items.size() > 0);

    mongoTemplateService.delete(savedItemSpices.getId());
    mongoTemplateService.delete(savedItemSnacks.getId());

  }

  @Test
  void testSearchItemWithCategory() {
    Item savedItemSpices = mongoTemplateService.add(itemSpices);
    Item savedItemSnacks = mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.search(null, null,
        null, searchCategory);

    Assertions.assertTrue(items.size() > 0);

    mongoTemplateService.delete(savedItemSpices.getId());
    mongoTemplateService.delete(savedItemSnacks.getId());

  }

  @Test
  void testSearchItemWithAll() {
    Item savedItemSpices = mongoTemplateService.add(itemSpices);
    Item savedItemSnacks = mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.search("Dried Red " +
            "Chilli", 1,
        5, searchCategory);

    Assertions.assertTrue(items.size() > 0);

    mongoTemplateService.delete(savedItemSpices.getId());
    mongoTemplateService.delete(savedItemSnacks.getId());

  }

  @Test
  void testFindAllWithPagination() {
    Item savedItemSpices = mongoTemplateService.add(itemSpices);
    Item savedItemSnacks = mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.getAll(pageNumber, pageSize);

    Assertions.assertTrue(items.size() > 0);

    mongoTemplateService.delete(savedItemSpices.getId());
    mongoTemplateService.delete(savedItemSnacks.getId());

  }

  @Test
  void testDeleteItemById() {
    Item savedItemSpices = mongoTemplateService.add(itemSpices);

    boolean result = mongoTemplateService.delete(savedItemSpices.getId());

    Assertions.assertEquals(result, true);

  }

  @Test
  void testDeleteItem() {
    Item savedItemSpices = mongoTemplateService.add(itemSpices);

    boolean result = mongoTemplateService.delete(savedItemSpices);

    Assertions.assertEquals(result, true);

  }
}