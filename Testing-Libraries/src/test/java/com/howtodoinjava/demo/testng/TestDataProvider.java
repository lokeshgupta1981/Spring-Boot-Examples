package com.howtodoinjava.demo.testng;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;

public class TestDataProvider {

  @DataProvider(name = "create")
  public static Object[][] createData() {
    return new Object[][]{
        new Object[]{1, "one"},
        new Object[]{2, "two"},
        new Object[]{3, "three"},
        new Object[]{4, "four"}
    };
  }

  @DataProvider(name = "createWithIterator")
  public Iterator<Object[]> createDataWithIterator() {
    return Arrays.asList(
        new Object[]{1, "one"},
        new Object[]{2, "two"},
        new Object[]{3, "three"},
        new Object[]{4, "four"}
    ).iterator();
  }

  @Test(dataProvider = "createWithIterator")
  public void test(Integer num, String display) {
    System.out.println("num is: " + num + " and display is: " + display);
  }

  @DataProvider(name = "data-provider")
  public Object[][] dataProviderMethod() {
    return new Object[][]{{"data one"}, {"data two"}};
  }

  @Test(dataProvider = "data-provider")
  public void testMethod(String data) {
    System.out.println("Data is: " + data);
  }

}
