package com.howtodoinjava.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {}
