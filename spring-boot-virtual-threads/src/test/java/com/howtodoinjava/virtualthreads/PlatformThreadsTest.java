package com.howtodoinjava.virtualthreads;

import com.howtodoinjava.virtualthreads.model.WeatherReport;
import com.howtodoinjava.virtualthreads.mvc.ReportAuditService;
import com.howtodoinjava.virtualthreads.mvc.WeatherApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = WeatherApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.threads.virtual.enabled=false")
class PlatformThreadsTest {

  @DynamicPropertySource
  static void stub(DynamicPropertyRegistry registry) {
    registry.add("weather.stub-url", TestStub::url);
  }

  @LocalServerPort
  int port;

  @Autowired
  ReportAuditService auditService;

  @Autowired
  AsyncTaskExecutor applicationTaskExecutor;

  @Test
  void requestRunsOnTomcatPoolThread() {
    WeatherReport report = RestClient.create("http://localhost:" + port)
        .get().uri("/weather/london").retrieve().body(WeatherReport.class);

    assertThat(report.virtual()).isFalse();
    assertThat(report.thread()).startsWith("Thread[").contains(",http-nio-").contains("-exec-");
    System.out.println("Request thread: " + report.thread());
  }

  @Test
  void asyncMethodRunsOnPoolThread() throws Exception {
    Thread thread = auditService.audit(new WeatherReport("london", 18, "cloudy", 42, "", false)).get();

    assertThat(thread.isVirtual()).isFalse();
    assertThat(thread.getName()).startsWith("task-");
    assertThat(applicationTaskExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);
    System.out.println("@Async thread: " + thread);
  }
}
