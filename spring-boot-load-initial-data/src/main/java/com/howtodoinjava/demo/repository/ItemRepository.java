package com.howtodoinjava.demo.repository;

import com.howtodoinjava.demo.entity.Item;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {
  public Optional<Item> findByName(String name);
}
