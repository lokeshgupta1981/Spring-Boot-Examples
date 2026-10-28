package com.howtodoinjava.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class InventoryItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank @Size(max = 80)
  private String name;

  @Min(0)
  private int quantity;

  public Long getId() { return id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
}
