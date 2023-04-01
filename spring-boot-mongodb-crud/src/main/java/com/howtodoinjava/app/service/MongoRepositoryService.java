package com.howtodoinjava.app.service;

import com.howtodoinjava.app.model.Item;

import java.util.List;

public interface MongoRepositoryService {

  public Item add(Item item);

  public Item update(Item itemToUpdate);

  public boolean delete(String id);

  public List<Item> getAll();

  public List<Item> getAll(Integer page, Integer size);

  public Item getById(String id);

  public Item findByName(String name);

  public List<Item> findAllByCategory(String category);

  public List<Item> findAllByQuantityBetween(int qtyGT, int qtyLT);
}
