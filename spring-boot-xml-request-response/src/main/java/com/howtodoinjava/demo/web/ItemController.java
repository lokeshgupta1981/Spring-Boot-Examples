package com.howtodoinjava.demo.web;

import com.howtodoinjava.demo.model.Item;
import com.howtodoinjava.demo.model.Items;
import com.howtodoinjava.demo.model.Record;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
public class ItemController {

  @GetMapping
  public Items getItems(){
    return new Items(List.of(new Item(1L, "Item1"), new Item(2L, "Item2")));
  }

  @GetMapping("/record")
  public Record getItemsAsRecord(){
    return new Record(1L, List.of(new Item(1L, "Item1"), new Item(2L, "Item2")));
  }

  @GetMapping("/{id}")
  public Item getItemById(@PathVariable("id") Long id){
    return new Item(id, "temp-item");
  }

  @PostMapping
  public Item createItem(@RequestBody Item item){
    return item;
  }
}
