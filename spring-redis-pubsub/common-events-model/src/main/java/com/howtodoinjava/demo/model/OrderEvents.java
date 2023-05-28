package com.howtodoinjava.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvents implements Serializable {

    private int orderId;
    private int userId;
    private String productName;
    private int price;
    private int quantity;
}
