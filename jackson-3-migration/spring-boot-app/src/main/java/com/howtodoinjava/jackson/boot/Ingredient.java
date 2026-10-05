package com.howtodoinjava.jackson.boot;

import java.time.LocalDate;

/** Fields declared as name, grams, addedOn; no all-args constructor. */
public class Ingredient {

  private String name;
  private int grams;
  private LocalDate addedOn;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getGrams() {
    return grams;
  }

  public void setGrams(int grams) {
    this.grams = grams;
  }

  public LocalDate getAddedOn() {
    return addedOn;
  }

  public void setAddedOn(LocalDate addedOn) {
    this.addedOn = addedOn;
  }
}
