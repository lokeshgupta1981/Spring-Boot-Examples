package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.GroceryItem;
import com.howtodoinjava.app.service.MongoRepositoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoRepositoryServiceImplTest {

    @Autowired
    private MongoRepositoryService mongoRepositoryService;

    GroceryItem groceryItem;
    GroceryItem updateGroceryItem;

    @BeforeEach
    void setUp() {
        groceryItem = new GroceryItem();
        groceryItem.setId(1);
        groceryItem.setName("Whole Wheat Biscuit");
        groceryItem.setQuantity(5);
        groceryItem.setCategory("snacks");

        updateGroceryItem = new GroceryItem();
        updateGroceryItem.setId(1);
        updateGroceryItem.setQuantity(2);
        updateGroceryItem.setCategory("snacks");
        updateGroceryItem.setName("Whole Wheat Biscuit");

    }

    @AfterEach
    void tearDown() {
        groceryItem = null;
        updateGroceryItem = null;
    }

    @Test
    @Order(1)
    void addGrocery() {

        GroceryItem saveGrocery = mongoRepositoryService.addGrocery(groceryItem);

        Assert.assertEquals(saveGrocery.getId(), groceryItem.getId());
    }

    @Test
    @Order(2)
    void updateGrocery() {
        GroceryItem updatedGrocery = mongoRepositoryService.updateGrocery(updateGroceryItem);

        Assert.assertEquals(updatedGrocery.getQuantity(), updateGroceryItem.getQuantity());
    }

    @Test
    @Order(3)
    void getAllGroceries() {
        List<GroceryItem> groceryItems = mongoRepositoryService.getAllGroceries();
        Assert.assertTrue(groceryItems.size() > 0);
    }

    @Test
    @Order(4)
    void getGroceryById() {
        GroceryItem item = mongoRepositoryService.getGroceryById(1);
        Assert.assertEquals(item.getName(), groceryItem.getName());
    }

    @Test
    @Order(5)
    void findItemByName() {
        GroceryItem item = mongoRepositoryService.findItemByName(groceryItem.getName());
        Assert.assertEquals(item.getName(), groceryItem.getName());
        Assert.assertEquals(item.getId(), groceryItem.getId());
    }

    @Test
    @Order(6)
    void findAllByCategory() {
        List<GroceryItem> items = mongoRepositoryService.findAllByCategory(groceryItem.getCategory());
        GroceryItem item = items.get(0);
        Assert.assertNotNull(item.getName());
        Assert.assertNotNull(item.getQuantity());
        Assert.assertNull(item.getCategory());
    }

    @Test
    @Order(7)
    void findGroceryItemByQuantityBetween() {
        List<GroceryItem> items = mongoRepositoryService.findAllByQuantityBetween(1, 5);
        Assert.assertTrue(items.size() > 0);
    }

    @Test
    @Order(8)
    void deleteGrocery() {
       String response = mongoRepositoryService.deleteGrocery(1);
        Assert.assertEquals(response, "Success");
    }


}