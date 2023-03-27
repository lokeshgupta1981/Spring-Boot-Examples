package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.GroceryItem;
import com.howtodoinjava.app.repositories.ItemRepository;
import com.howtodoinjava.app.service.MongoRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoRepositoryServiceImpl implements MongoRepositoryService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public GroceryItem addGrocery(GroceryItem groceryItem) {
        return itemRepository.save(groceryItem);
    }

    @Override
    public GroceryItem updateGrocery(GroceryItem groceryItem) {
        return itemRepository.save(groceryItem);
    }

    @Override
    public List<GroceryItem> getAllGroceries() {
        return itemRepository.findAll();
    }

    @Override
    public GroceryItem getGroceryById(Integer id) {
        return itemRepository.findById(id).orElse(null);
    }

    @Override
    public GroceryItem findItemByName(String name) {
        return itemRepository.findItemByName(name);
    }

    @Override
    public List<GroceryItem> findAllByCategory(String category) {
        return itemRepository.findGroceryItemBy(category);
    }

    @Override
    public List<GroceryItem> findAllByQuantityBetween(int qtyGT, int qtyLT) {
        return itemRepository.findAllByQuantityBetween(qtyGT, qtyLT);
    }

    @Override
    public String deleteGrocery(Integer id) {
        itemRepository.deleteById(id);
        return "Success";
    }
}
