package com.howtodoinjava.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryFlowTest {
  @Autowired MockMvc mvc;
  @Autowired InventoryRepository repository;

  @BeforeEach void clear() { repository.deleteAll(); }

  @Test void createEditAndDeleteItem() throws Exception {
    mvc.perform(get("/items")).andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("No items yet")));

    mvc.perform(post("/items").contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("name", "Notebook").param("quantity", "5"))
        .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/items"));
    InventoryItem item = repository.findAll().get(0);
    assertThat(item.getName()).isEqualTo("Notebook");
    mvc.perform(get("/items")).andExpect(content().string(org.hamcrest.Matchers.containsString("Notebook")));

    mvc.perform(post("/items/{id}", item.getId()).param("name", "Planner").param("quantity", "7"))
        .andExpect(status().is3xxRedirection());
    assertThat(repository.findById(item.getId()).orElseThrow().getQuantity()).isEqualTo(7);

    mvc.perform(post("/items/{id}/delete", item.getId())).andExpect(status().is3xxRedirection());
    assertThat(repository.count()).isZero();
  }

  @Test void invalidFormStaysOnFormAndDoesNotSave() throws Exception {
    mvc.perform(get("/items/new")).andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("action=\"/items\"")));
    mvc.perform(post("/items").param("name", " ").param("quantity", "-1"))
        .andExpect(status().isOk()).andExpect(view().name("items/form"))
        .andExpect(model().attributeHasFieldErrors("item", "name", "quantity"));
    assertThat(repository.count()).isZero();
  }

  @Test void missingItemReturns404() throws Exception {
    mvc.perform(get("/items/999/edit")).andExpect(status().isNotFound());
    mvc.perform(post("/items/999/delete")).andExpect(status().isNotFound());
  }

  @Test void invalidEditKeepsOriginalRowAndEditAction() throws Exception {
    InventoryItem item = new InventoryItem();
    item.setName("Notebook");
    item.setQuantity(5);
    item = repository.save(item);
    mvc.perform(post("/items/{id}", item.getId()).param("name", " ").param("quantity", "-1"))
        .andExpect(status().isOk()).andExpect(view().name("items/form"))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("action=\"/items/" + item.getId() + "\"")));
    assertThat(repository.findById(item.getId()).orElseThrow().getName()).isEqualTo("Notebook");
  }
}
