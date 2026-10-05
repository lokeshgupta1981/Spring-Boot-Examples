package com.howtodoinjava.jackson.model;

public enum Difficulty {
  EASY, HARD;

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
