package com.howtodoinjava.virtualthreads;

import com.howtodoinjava.virtualthreads.stub.StubApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/** Starts the downstream stub once per test JVM on a random port with a 200 ms delay. */
public final class TestStub {

  public static final long DELAY_MS = 200;
  private static ConfigurableApplicationContext context;

  private TestStub() {
  }

  public static synchronized String url() {
    if (context == null) {
      context = new SpringApplicationBuilder(StubApplication.class)
          .web(WebApplicationType.REACTIVE)
          .profiles("stub")
          .properties("server.port=0", "stub.delay=" + DELAY_MS + "ms")
          .run();
    }
    int port = ((WebServerApplicationContext) context).getWebServer().getPort();
    return "http://localhost:" + port;
  }
}
