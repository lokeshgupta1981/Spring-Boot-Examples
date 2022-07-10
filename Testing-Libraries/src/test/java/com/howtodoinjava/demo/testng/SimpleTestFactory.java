package com.howtodoinjava.demo.testng;

import org.testng.annotations.Factory;

public class SimpleTestFactory {
  @Factory
  public Object[] factoryMethod() {
    return new Object[]{new SimpleTest(0), new SimpleTest(1)};
  }
}