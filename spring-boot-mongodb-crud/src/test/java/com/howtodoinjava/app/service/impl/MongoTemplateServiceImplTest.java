package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.GroceryItem;
import com.howtodoinjava.app.service.MongoTemplateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoTemplateServiceImplTest {

    @Autowired
    MongoTemplateService mongoTemplateService;

    GroceryItem groceryItem;
    GroceryItem updateGroceryItem;
    Integer pageNumber = 1;
    Integer pageSize = 5;

    @BeforeEach
    void setUp() {
        groceryItem = new GroceryItem();
        groceryItem.setId(2);
        groceryItem.setName("Dried Red Chilli");
        groceryItem.setQuantity(3);
        groceryItem.setCategory("spices");

        updateGroceryItem = new GroceryItem();
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

        GroceryItem saveGrocery = mongoTemplateService.addGrocery(groceryItem);

        Assert.assertEquals(saveGrocery.getId(), groceryItem.getId());
    }

    @Test
    @Order(2)
    void updateGrocery() {
        GroceryItem updatedGrocery = mongoTemplateService.updateGrocery(updateGroceryItem);

        Assert.assertEquals(updatedGrocery.getQuantity(), updateGroceryItem.getQuantity());
    }

    @Test
    @Order(3)
    void getAllGroceries() {
        List<GroceryItem> groceryItems = mongoTemplateService.getAllGroceries();
        Assert.assertTrue(groceryItems.size() > 0);
    }

    @Test
    @Order(4)
    void getGroceryById() {
        GroceryItem item = mongoTemplateService.getGroceryById(2);
        Assert.assertEquals(item.getName(), groceryItem.getName());
    }

    @Test
    @Order(5)
    void searchGroceriesWithName() {
        List<GroceryItem> items = mongoTemplateService.searchGroceries("Dried Red Chilli", null,
                null, null);

        Assert.assertTrue(items.size() > 0);
    }

    @Test
    @Order(6)
    void searchGroceriesWithQuantity() {
        List<GroceryItem> items = mongoTemplateService.searchGroceries(null, 1,
                5, null);

        Assert.assertTrue(items.size() > 0);
    }

    @Test
    @Order(7)
    void searchGroceriesWithCategory() {
        List<GroceryItem> items = mongoTemplateService.searchGroceries(null, null,
                null, "spices");

        Assert.assertTrue(items.size() > 0);
    }

    @Test
    @Order(8)
    void searchGroceriesWithAll() {
        List<GroceryItem> items = mongoTemplateService.searchGroceries("Dried Red Chilli", 1,
                5, "spices");

        Assert.assertTrue(items.size() > 0);
    }

    @Test
    @Order(9)
    void deleteGrocery() {
        String response = mongoTemplateService.deleteGrocery(2);
        Assert.assertEquals(response, "Success");
    }
}