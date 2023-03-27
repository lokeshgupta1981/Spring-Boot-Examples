package com.howtodoinjava.app.service;

import com.howtodoinjava.app.model.Item;

import java.util.List;

public interface MongoTemplateService {

  public Item add(Item item);

  public Item update(Item item);

  public boolean delete(Integer id);

  public List<Item> getAll();

  public Item getById(Integer id);

  public List<Item> search(String name, Integer minQuantity,
                           Integer maxQuantity,
                           String category);
}
