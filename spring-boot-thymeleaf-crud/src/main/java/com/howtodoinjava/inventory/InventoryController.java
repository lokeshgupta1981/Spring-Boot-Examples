package com.howtodoinjava.inventory;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/items")
public class InventoryController {
  private final InventoryRepository repository;

  public InventoryController(InventoryRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("items", repository.findAll());
    return "items/list";
  }

  @GetMapping("/new")
  public String newForm(Model model) {
    model.addAttribute("item", new InventoryItem());
    return "items/form";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("item", findItem(id));
    model.addAttribute("itemId", id);
    return "items/form";
  }

  @PostMapping
  public String create(@Valid @ModelAttribute("item") InventoryItem item,
                       BindingResult errors) {
    if (errors.hasErrors()) return "items/form";
    repository.save(item);
    return "redirect:/items";
  }

  @PostMapping("/{id}")
  public String update(@PathVariable Long id,
                       @Valid @ModelAttribute("item") InventoryItem input,
                       BindingResult errors, Model model) {
    InventoryItem existing = findItem(id);
    if (errors.hasErrors()) {
      model.addAttribute("itemId", id);
      return "items/form";
    }
    existing.setName(input.getName());
    existing.setQuantity(input.getQuantity());
    repository.save(existing);
    return "redirect:/items";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id) {
    repository.delete(findItem(id));
    return "redirect:/items";
  }

  private InventoryItem findItem(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
  }
}
