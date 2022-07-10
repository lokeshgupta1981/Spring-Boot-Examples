package com.howtodoinjava.demo.testng;

import org.testng.annotations.Test;

import java.io.IOException;

public class TestNgExpectedException {
  @Test(expectedExceptions = {IOException.class},
      expectedExceptionsMessageRegExp = "Pass Message test")
  public void exceptionTestOne() throws Exception {
    throw new IOException("Pass Message test");
  }

  @Test(expectedExceptions = {IOException.class},
      expectedExceptionsMessageRegExp = ".* Message .*")
  public void exceptionTestTwo() throws Exception {
    throw new IOException("Pass Message test");
  }

  @Test(expectedExceptions = {IOException.class},
      expectedExceptionsMessageRegExp = "Pass Message test")
  public void exceptionTestThree() throws Exception {
    throw new IOException("Fail Message test");   //Fails
  }
}
