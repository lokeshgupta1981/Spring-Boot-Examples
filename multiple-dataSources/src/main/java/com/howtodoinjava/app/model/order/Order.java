package com.howtodoinjava.app.model.order;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_orders", schema = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private Integer id;

  @Column(name = "product_name")
  private String productName;

  @Column(name = "order_amount")
  private Integer orderAmount;

  @Column(name = "user_id")
  private Integer userId;
}
