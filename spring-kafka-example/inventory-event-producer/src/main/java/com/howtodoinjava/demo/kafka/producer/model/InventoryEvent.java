package com.howtodoinjava.demo.kafka.producer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryEvent {
    private Integer inventoryId;
    private InventoryEventType inventoryEventType;
    private Product product;
}
