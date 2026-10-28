package com.howtodoinjava.warehouse;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StockItemRepositoryTest {
  @Autowired StockItemRepository repository;
  @Autowired EntityManager entityManager;

  @Test void savesAndReadsAnItem() {
    StockItem saved = repository.saveAndFlush(new StockItem("SKU-100", 12));
    assertThat(saved.getId()).isNotNull();
    entityManager.clear();
    assertThat(repository.findById(saved.getId())).get()
        .extracting(StockItem::getSku, StockItem::getQuantity)
        .containsExactly("SKU-100", 12);
  }
}
