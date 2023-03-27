package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.service.MongoTemplateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoTemplateServiceImplTest {

  //TODO: same changes as other test class

  @Autowired
  MongoTemplateService mongoTemplateService;

  Item groceryItem;
  Item updateGroceryItem;
  Integer pageNumber = 1;
  Integer pageSize = 5;

  @BeforeEach
  void setUp() {
    groceryItem = new Item();
    groceryItem.setId(2);
    groceryItem.setName("Dried Red Chilli");
    groceryItem.setQuantity(3);
    groceryItem.setCategory("spices");

    updateGroceryItem = new Item();
    updateGroceryItem.setId(2);
    updateGroceryItem.setQuantity(1);
    updateGroceryItem.setCategory("spices");
    updateGroceryItem.setName("Dried Red Chilli");
  }

  @AfterEach
  void tearDown() {
    groceryItem = null;
    updateGroceryItem = null;
  }


  @Test
  @Order(1)
  void addGrocery() {

    Item saveGrocery = mongoTemplateService.add(groceryItem);

    Assert.assertEquals(saveGrocery.getId(), groceryItem.getId());
  }

  @Test
  @Order(2)
  void updateGrocery() {
    Item updatedGrocery = mongoTemplateService.update(updateGroceryItem);

    Assert.assertEquals(updatedGrocery.getQuantity(),
        updateGroceryItem.getQuantity());
  }

  @Test
  @Order(3)
  void getAllGroceries() {
    List<Item> groceryItems = mongoTemplateService.getAll();
    Assert.assertTrue(groceryItems.size() > 0);
  }

  @Test
  @Order(4)
  void getGroceryById() {
    Item item = mongoTemplateService.getById(2);
    Assert.assertEquals(item.getName(), groceryItem.getName());
  }

  @Test
  @Order(5)
  void searchGroceriesWithName() {
    List<Item> items = mongoTemplateService.search("Dried Red " +
            "Chilli", null,
        null, null);

    Assert.assertTrue(items.size() > 0);
  }

  @Test
  @Order(6)
  void searchGroceriesWithQuantity() {
    List<Item> items = mongoTemplateService.search(null, 1,
        5, null);

    Assert.assertTrue(items.size() > 0);
  }

  @Test
  @Order(7)
  void searchGroceriesWithCategory() {
    List<Item> items = mongoTemplateService.search(null, null,
        null, "spices");

    Assert.assertTrue(items.size() > 0);
  }

  @Test
  @Order(8)
  void searchGroceriesWithAll() {
    List<Item> items = mongoTemplateService.search("Dried Red " +
            "Chilli", 1,
        5, "spices");

    Assert.assertTrue(items.size() > 0);
  }
}