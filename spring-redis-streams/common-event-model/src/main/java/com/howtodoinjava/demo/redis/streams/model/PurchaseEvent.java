package com.howtodoinjava.demo.redis.streams.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseEvent {

    private String purchaseId;
    private String productId;
    private String productName;
    private int price;
    private int quantity;
}
