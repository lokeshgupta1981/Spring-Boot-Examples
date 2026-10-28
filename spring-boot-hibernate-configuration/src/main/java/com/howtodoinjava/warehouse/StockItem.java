package com.howtodoinjava.warehouse;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class StockItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String sku;
  private int quantity;

  protected StockItem() {}

  public StockItem(String sku, int quantity) {
    this.sku = sku;
    this.quantity = quantity;
  }

  public Long getId() { return id; }
  public String getSku() { return sku; }
  public int getQuantity() { return quantity; }
}
