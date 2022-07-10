package com.howtodoinjava.demo.testng;

import org.testng.annotations.Test;

public class SimpleTest {
  private int param;

  public SimpleTest(int param) {
    this.param = param;
  }

  @Test
  public void testMethodOne() {
    int opValue = param + 1;
    System.out.println("Test method one output: " + opValue);
  }

  @Test
  public void testMethodTwo() {
    int opValue = param + 2;
    System.out.println("Test method two output: " + opValue);
  }
}
