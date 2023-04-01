package com.howtodoinjava.app.service;

import com.howtodoinjava.app.model.Item;

import java.util.List;

public interface MongoTemplateService {

  public Item add(Item item);

  public Item update(Item item);

  public boolean delete(String id);

  public boolean delete(Item item);

  public List<Item> getAll();

  public List<Item> getAll(Integer page, Integer size);

  public Item getById(String id);

  public List<Item> search(String name, Integer minQuantity,
                           Integer maxQuantity,
                           String category);
}
