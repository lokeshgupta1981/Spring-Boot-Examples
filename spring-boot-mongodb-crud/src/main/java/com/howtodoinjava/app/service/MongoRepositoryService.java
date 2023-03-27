package com.howtodoinjava.app.service;

import com.howtodoinjava.app.model.Item;

import java.util.List;

public interface MongoRepositoryService {

  public Item add(Item groceryItem);

  public Item update(Item groceryItem);

  public boolean delete(Integer id);

  public List<Item> getAll();

  public Item getById(Integer id);

  public Item findByName(String name);

  public List<Item> findAllByCategory(String category);

  public List<Item> findAllByQuantityBetween(int qtyGT, int qtyLT);
}
