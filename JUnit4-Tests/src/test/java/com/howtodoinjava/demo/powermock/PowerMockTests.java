package com.howtodoinjava.demo.powermock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Service.class})
public class PowerMockTests {

  @Test
  public void mockStaticMethodTest() {
    //Mock static method
    PowerMockito.mockStatic(Service.class);

    //Set expectation
    Mockito.when(Service.staticMessage()).thenReturn("New Message from Mock!");

    //invoke the method
    String message = Service.staticMessage();

    //Assert the stub response
    Assert.assertEquals(message, "New Message from Mock!");

    //Verify static method invocation
    PowerMockito.verifyStatic(Service.class, times(1));
    Service.staticMessage();
  }

  @Test
  public void mockFinalMethodTest() {
    //Mock final method
    Service serviceMock = PowerMockito.mock(Service.class, Mockito
        .withSettings()
        .name("ServiceMock")
        .verboseLogging());

    //Set expectation
    Mockito.when(serviceMock.finalMessage()).thenReturn("New Message from " +
        "Mock!");

    //invoke the method
    String message = serviceMock.finalMessage();

    //Assert the stub response
    Assert.assertEquals(message, "New Message from Mock!");

    //Verify final method invocation
    Mockito.verify(serviceMock).finalMessage();
  }

  @Test
  public void mockPrivateMethodTest() throws Exception {
    Service mock = PowerMockito.spy(new Service());
    PowerMockito.doReturn("New Message from Mock!").when(mock,"privateMessage");
    String privateMessage = Whitebox.invokeMethod(mock, "privateMessage");
    Assert.assertEquals(privateMessage, "New Message from Mock!");

    PowerMockito.verifyPrivate(mock, times(1)).invoke("privateMessage");
  }
}

class Service {
  private String privateMessage() {
    return "Hello World!";
  }

  public static String staticMessage() {
    return "Hello World!";
  }

  public final String finalMessage() {
    return "Hello World!";
  }
}