package com.howtodoinjava.integration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CustomerOrder {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String item;
  private String status;

  protected CustomerOrder() {}
  public CustomerOrder(String item, String status) {
    this.item = item;
    this.status = status;
  }
  public Long getId() { return id; }
  public String getItem() { return item; }
  public String getStatus() { return status; }
}
