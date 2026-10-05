package com.howtodoinjava.boot4;

public record PantryItemV1(String name, int quantity) {

  static PantryItemV1 from(PantryItem item) {
    return new PantryItemV1(item.name(), item.quantity());
  }
}
