package com.howtodoinjava.demo.testng;

import org.testng.annotations.Test;

@Test(timeOut = 500)
public class TestTimeout {
  @Test
  public void timeTestOne() throws InterruptedException {          //Fails
    Thread.sleep(1000);
    System.out.println("Time test method one");
  }

  @Test
  public void timeTestTwo() throws InterruptedException {
    Thread.sleep(400);
    System.out.println("Time test method two");
  }
}
