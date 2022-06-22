package com.howtodoinjava.demo.powermock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Service.class})
public class PowerMockTests {

  @Test
  public void mockStaticMethodTest() {
    PowerMockito.mockStatic(Service.class);
    Mockito.when(Service.message()).thenReturn("New Message!");
    String message = Service.message();
    Assert.assertEquals(message, "New Message!");
  }
}

class Service {
  public static String message() {
    return "Hello World!";
  }
}