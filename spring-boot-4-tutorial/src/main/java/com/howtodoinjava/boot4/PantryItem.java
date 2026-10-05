package com.howtodoinjava.boot4;

import java.time.LocalDate;

public record PantryItem(String name, int quantity, LocalDate bestBefore) {
}
