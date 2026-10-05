package com.howtodoinjava.jackson.legacy;
import java.time.LocalDate;
public record Recipe(String name, int servings, LocalDate createdOn) {}
