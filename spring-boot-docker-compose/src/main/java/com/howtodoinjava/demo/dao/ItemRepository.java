package com.howtodoinjava.demo.dao;

import com.howtodoinjava.demo.dao.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Long> {
}
