package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.repositories.ItemRepository;
import com.howtodoinjava.app.service.MongoRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoRepositoryServiceImpl implements MongoRepositoryService {

  @Autowired
  private ItemRepository itemRepository;

  @Override
  public Item add(Item item) {
    return itemRepository.save(item);
  }

  @Override
  public Item update(Item itemToUpdate) {
    return itemRepository.save(itemToUpdate);
  }

  @Override
  public List<Item> getAll() {
    return itemRepository.findAll();
  }

  @Override
  public List<Item> getAll(Integer page, Integer size) {

    Sort sort = Sort.by("name").ascending();
    Pageable pageable = PageRequest.of(page - 1, size, sort);
    return itemRepository.findAll(pageable).getContent();
  }

  @Override
  public Item getById(String id) {
    return itemRepository.findById(id).orElse(null);
  }

  @Override
  public Item findByName(String name) {
    return itemRepository.findByName(name);
  }

  @Override
  public List<Item> findAllByCategory(String category) {
    return itemRepository.findByCategory(category);
  }

  @Override
  public List<Item> findAllByQuantityBetween(int qtyGT, int qtyLT) {
    return itemRepository.findAllByQuantityBetween(qtyGT, qtyLT);
  }

  @Override
  public boolean delete(String id) {
    itemRepository.deleteById(id);
    return true;
  }
}
