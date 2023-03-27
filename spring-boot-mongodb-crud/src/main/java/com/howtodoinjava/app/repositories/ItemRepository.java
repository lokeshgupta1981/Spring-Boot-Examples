package com.howtodoinjava.app.repositories;

import com.howtodoinjava.app.model.GroceryItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemRepository extends MongoRepository<GroceryItem, Integer> {
    
    @Query("{name:'?0'}")
    GroceryItem findItemByName(String name);
    
    @Query(value="{category:'?0'}", fields="{'name' : 1, 'quantity' : 1}")
    List<GroceryItem> findGroceryItemBy(String category);

    @Query("{ 'quantity' : { $gt: ?0, $lt: ?1 } }")
    List<GroceryItem> findAllByQuantityBetween(int qtyGT, int qtyLT);

}