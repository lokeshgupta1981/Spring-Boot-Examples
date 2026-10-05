package com.howtodoinjava.jackson.model;

/** Primitive field: JSON null for "grams" fails in Jackson 3 by default. */
public class Portion {

  private int grams;

  public int getGrams() {
    return grams;
  }

  public void setGrams(int grams) {
    this.grams = grams;
  }
}
