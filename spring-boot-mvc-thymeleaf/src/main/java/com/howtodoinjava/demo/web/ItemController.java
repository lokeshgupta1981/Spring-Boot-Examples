package com.howtodoinjava.demo.web;

import com.howtodoinjava.demo.dao.ItemRepository;
import com.howtodoinjava.demo.dao.model.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/items")
public class ItemController {

  @Autowired
  ItemRepository itemRepository;

  @GetMapping
  String getAll(Model model) {
    List<Item> items = (List<Item>) itemRepository.findAll();
    model.addAttribute("items", items);
    return "items";
  }

  @GetMapping("/{id}")
  String get(@PathVariable("id") long id, Model model) {
    Optional<Item> item = itemRepository.findById(id);
    if (item.isPresent()) {
      model.addAttribute("item", item);
    }
    return "item-detail";
  }
}
