package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.service.MongoTemplateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.util.List;

@SpringBootTest
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
    itemSpices.setId(3);
    itemSpices.setName("Dried Red Chilli");
    itemSpices.setQuantity(3);
    itemSpices.setCategory("spices");

    itemToUpdate = new Item();
    itemToUpdate.setId(3);
    itemToUpdate.setQuantity(1);
    itemToUpdate.setCategory("spices");
    itemToUpdate.setName("Dried Red Chilli");

    itemSnacks = new Item();
    itemSnacks.setId(4);
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

    Assert.assertEquals(savedItem.getId(), itemSpices.getId());
    Assert.assertEquals(savedItem.getName(), itemSpices.getName());
    Assert.assertEquals(savedItem.getQuantity(), itemSpices.getQuantity());
    Assert.assertEquals(savedItem.getCategory(), itemSpices.getCategory());

    mongoTemplateService.delete(savedItem.getId());
  }

  @Test
  void testUpdateItem() {
    mongoTemplateService.add(itemSpices);

    Item updatedItem = mongoTemplateService.update(itemToUpdate);

    Assert.assertEquals(updatedItem.getQuantity(),
            itemToUpdate.getQuantity());

    mongoTemplateService.delete(updatedItem.getId());
  }

  @Test
  void testGetAllItems() {
    mongoTemplateService.add(itemSpices);
    mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.getAll();
    Assert.assertTrue(items.size() == 2);

    mongoTemplateService.delete(itemSpices.getId());
    mongoTemplateService.delete(itemSnacks.getId());

  }

  @Test
  void testGetItemById() {
    mongoTemplateService.add(itemSpices);

    Item item = mongoTemplateService.getById(3);

    Assert.assertEquals(item.getName(), item.getName());

  }

  @Test
  void testSearchItemWithName() {

    mongoTemplateService.add(itemSpices);
    mongoTemplateService.add(itemSnacks);


    List<Item> items = mongoTemplateService.search("Dried Red " +
            "Chilli", null,
        null, null);

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(itemSpices.getId());
    mongoTemplateService.delete(itemSnacks.getId());
  }

  @Test
  void testSearchItemWithQuantity() {
    mongoTemplateService.add(itemSpices);
    mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.search(null, 1,
        5, null);

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(itemSpices.getId());
    mongoTemplateService.delete(itemSnacks.getId());

  }

  @Test
  void testSearchItemWithCategory() {
    mongoTemplateService.add(itemSpices);
    mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.search(null, null,
        null, searchCategory);

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(itemSpices.getId());
    mongoTemplateService.delete(itemSnacks.getId());

  }

  @Test
  void testSearchItemWithAll() {
    mongoTemplateService.add(itemSpices);
    mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.search("Dried Red " +
            "Chilli", 1,
        5, searchCategory);

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(itemSpices.getId());
    mongoTemplateService.delete(itemSnacks.getId());

  }

  @Test
  void testFindAllWithPagination() {
    mongoTemplateService.add(itemSpices);
    mongoTemplateService.add(itemSnacks);

    List<Item> items = mongoTemplateService.getAll(pageNumber, pageSize);

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(itemSpices.getId());
    mongoTemplateService.delete(itemSnacks.getId());

  }

  @Test
  void testDeleteItemById() {
    mongoTemplateService.add(itemSpices);

    boolean result = mongoTemplateService.delete(itemSpices.getId());

    Assert.assertEquals(result, true);

  }
}