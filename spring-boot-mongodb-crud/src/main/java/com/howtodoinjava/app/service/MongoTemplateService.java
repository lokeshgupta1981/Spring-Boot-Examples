package com.howtodoinjava.app.service;

import com.howtodoinjava.app.model.GroceryItem;

import java.util.List;

public interface MongoTemplateService {

    public GroceryItem addGrocery(GroceryItem groceryItem);
    public GroceryItem updateGrocery(GroceryItem groceryItem);
    public String deleteGrocery(Integer id);
    public List<GroceryItem> getAllGroceries();
    public GroceryItem getGroceryById(Integer id);
    public List<GroceryItem> searchGroceries(String name, Integer minQuantity, Integer maxQuantity,
                                             String category);
}
