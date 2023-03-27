package com.howtodoinjava.app.service;

import com.howtodoinjava.app.model.GroceryItem;

import java.util.List;

public interface MongoRepositoryService {

    public GroceryItem addGrocery(GroceryItem groceryItem);
    public GroceryItem updateGrocery(GroceryItem groceryItem);
    public String deleteGrocery(Integer id);
    public List<GroceryItem> getAllGroceries();
    public GroceryItem getGroceryById(Integer id);
    public GroceryItem findItemByName(String name);
    public List<GroceryItem> findAllByCategory(String category);
    public List<GroceryItem> findAllByQuantityBetween(int qtyGT, int qtyLT);

}
