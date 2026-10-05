package com.howtodoinjava.jackson.model;

/**
 * Plain class (setters, no all-args constructor) with fields declared as name, grams, vegan.
 * Jackson 2 writes them in declaration order; Jackson 3 sorts them alphabetically.
 */
public class Ingredient {

  private String name;
  private int grams;
  private boolean vegan;

  public Ingredient() {
  }

  /**
   * Static factory instead of an all-args constructor: Jackson 3 writes creator (constructor)
   * properties first, in declaration order, so a class with an all-args constructor or a record
   * keeps its field order even with SORT_PROPERTIES_ALPHABETICALLY enabled.
   */
  public static Ingredient of(String name, int grams, boolean vegan) {
    Ingredient ingredient = new Ingredient();
    ingredient.name = name;
    ingredient.grams = grams;
    ingredient.vegan = vegan;
    return ingredient;
  }

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

  public boolean isVegan() {
    return vegan;
  }

  public void setVegan(boolean vegan) {
    this.vegan = vegan;
  }
}
