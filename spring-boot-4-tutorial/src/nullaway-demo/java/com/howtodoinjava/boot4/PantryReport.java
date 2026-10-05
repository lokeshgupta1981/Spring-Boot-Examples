package com.howtodoinjava.boot4;

/** Compiled only with -Pnullaway-demo: NullAway rejects the unchecked call on line 12. */
class PantryReport {

  private final PantryService pantryService = new PantryService();

  int quantityOf(String name) {
    PantryItem item = pantryService.find(name);  // @Nullable return type

    // NullAway: dereferenced expression item is @Nullable
    int quantity = item.quantity();
    return quantity;
  }
}
