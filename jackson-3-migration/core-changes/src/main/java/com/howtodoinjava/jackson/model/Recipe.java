package com.howtodoinjava.jackson.model;

import java.time.LocalDate;

public record Recipe(String name, int servings, LocalDate createdOn) {
}
