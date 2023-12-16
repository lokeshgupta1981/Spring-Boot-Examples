package com.howtodoinjava.demo.web.service;

import com.howtodoinjava.demo.web.model.Item;
import com.howtodoinjava.demo.web.util.TimeoutUtils;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

  public List<Item> getAll() {
    TimeoutUtils.busyOperation(10000);
    return List.of(new Item(1L, "Item-1"), new Item(2L, "Item-2"));
  }
}
