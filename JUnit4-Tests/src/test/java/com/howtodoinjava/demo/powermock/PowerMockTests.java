package com.howtodoinjava.demo.powermock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Service.class})
public class PowerMockTests {

  @Test
  public void mockStaticMethodTest() {
    PowerMockito.mockStatic(Service.class);
    Mockito.when(Service.message()).thenReturn("New Message!");
    String message = Service.message();
    Assert.assertEquals(message, "New Message!");
    //PowerMockito.verifyStatic(Service.class, Mockito.times(1));
  }
  @Test
  public void mockFinalMethodTest() {
    Service service = PowerMockito.mock(Service.class);
    Mockito.when(service.finalMessage()).thenReturn("Finally, New Message!");
    String message = service.finalMessage();
    Assert.assertEquals(message, "Finally, New Message!");
  }
  @Test
  public void mockPrivateMethodTest() throws Exception {
    Service mock = spy(new Service());
    PowerMockito.when(mock, "privateMessage").thenReturn("A Private Message!");
    String privateMessage = Whitebox.invokeMethod(mock, "privateMessage");
    Assert.assertEquals(privateMessage, "A Private Message!");
  }
}

class Service {
  private String privateMessage() {
    return "Hello World!";
  }
  private String privateMessage(String parameter) {
    return "Hello World! " + parameter;
  }
  public static String message() {
    return "Hello World!";
  }
  public final String finalMessage() {
    return "Hello World!";
  }
}