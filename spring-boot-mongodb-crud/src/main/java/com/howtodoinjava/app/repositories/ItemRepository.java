package com.howtodoinjava.app.repositories;

import com.howtodoinjava.app.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {

  @Query("{name:'?0'}")
  Item findByName(String name);

  @Query(value = "{category:'?0'}", fields = "{'name' : 1, 'quantity' : 1}")
  List<Item> findByCategory(String category);

  @Query("{ 'quantity' : { $gt: ?0, $lt: ?1 } }")
  List<Item> findAllByQuantityBetween(int qtyGT, int qtyLT);

}