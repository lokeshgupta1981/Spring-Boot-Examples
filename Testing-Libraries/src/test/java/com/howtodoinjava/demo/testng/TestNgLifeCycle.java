package com.howtodoinjava.demo.testng;

import org.testng.annotations.*;

public class TestNgLifeCycle {
  @Test
  public void testCase() {
  }

  @BeforeSuite
  public void beforeSuite() {
    System.out.println("Before Suite method");
  }

  @AfterSuite
  public void afterSuite() {
    System.out.println("After Suite method");
  }

  @BeforeTest
  public void beforeTest() {
    System.out.println("Before Test method");
  }

  @AfterTest
  public void afterTest() {
    System.out.println("After Test method");
  }

  @BeforeClass
  public void beforeClass() {
    System.out.println("Before Class method");
  }

  @AfterClass
  public void afterClass() {
    System.out.println("After Class method");
  }

  @BeforeMethod
  public void beforeMethod() {
    System.out.println("Before Method");
  }

  @AfterMethod
  public void afterMethod() {
    System.out.println("After Method");
  }
}
