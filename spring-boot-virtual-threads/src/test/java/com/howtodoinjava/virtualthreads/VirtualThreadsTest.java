package com.howtodoinjava.virtualthreads;

import com.howtodoinjava.virtualthreads.model.WeatherReport;
import com.howtodoinjava.virtualthreads.mvc.ReportAuditService;
import com.howtodoinjava.virtualthreads.mvc.WeatherApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = WeatherApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "weather.air-quality-limit=5")
class VirtualThreadsTest {

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

  @Autowired
  TaskScheduler taskScheduler;

  @Autowired
  ClientHttpRequestFactory clientHttpRequestFactory;

  RestClient api() {
    return RestClient.create("http://localhost:" + port);
  }

  @Test
  void requestRunsOnVirtualThread() {
    WeatherReport report = api().get().uri("/weather/london").retrieve().body(WeatherReport.class);

    assertThat(report.virtual()).isTrue();
    assertThat(report.thread()).startsWith("VirtualThread[").contains(",tomcat-handler-")
        .contains("@ForkJoinPool-1-worker-");
    assertThat(report.aqi()).isEqualTo(42);
    assertThat(report.temperature()).isEqualTo(18);
    System.out.println("Request thread: " + report.thread());
  }

  @Test
  void asyncMethodRunsOnVirtualThread() throws Exception {
    Thread thread = auditService.audit(new WeatherReport("london", 18, "cloudy", 42, "", true)).get();

    assertThat(thread.isVirtual()).isTrue();
    assertThat(thread.getName()).startsWith("task-");
    System.out.println("@Async thread: " + thread);
  }

  @Test
  void executorAndSchedulerUseVirtualThreads() {
    assertThat(applicationTaskExecutor).isInstanceOf(SimpleAsyncTaskExecutor.class);
    assertThat(taskScheduler).isInstanceOf(SimpleAsyncTaskScheduler.class);
    System.out.println("applicationTaskExecutor: " + applicationTaskExecutor.getClass().getSimpleName());
    System.out.println("taskScheduler: " + taskScheduler.getClass().getSimpleName());
  }

  @Test
  void jdkHttpClientUsesVirtualThreadExecutor() throws Exception {
    assertThat(clientHttpRequestFactory).isInstanceOf(JdkClientHttpRequestFactory.class);
    Field field = JdkClientHttpRequestFactory.class.getDeclaredField("httpClient");
    field.setAccessible(true);
    HttpClient httpClient = (HttpClient) field.get(clientHttpRequestFactory);
    Executor executor = httpClient.executor().orElseThrow();

    CompletableFuture<Thread> seen = new CompletableFuture<>();
    executor.execute(() -> seen.complete(Thread.currentThread()));
    Thread thread = seen.get();
    assertThat(thread.isVirtual()).isTrue();
    assertThat(thread.getName()).startsWith("httpclient-");
    System.out.println("HttpClient executor thread: " + thread);
  }

  @Test
  void parallelCallsTakeOneDelayInsteadOfTwo() {
    long sequential = bestOfThree("/weather/paris");
    long parallel = bestOfThree("/weather/paris/parallel");

    assertThat(sequential).isGreaterThanOrEqualTo(2 * TestStub.DELAY_MS);
    assertThat(parallel).isGreaterThanOrEqualTo(TestStub.DELAY_MS).isLessThan(2 * TestStub.DELAY_MS);
    System.out.println("sequential " + sequential + " ms, parallel " + parallel + " ms");
  }

  @Test
  void semaphoreLimitsConcurrentAirQualityCalls() throws Exception {
    List<Future<WeatherReport>> results = new ArrayList<>();
    try (ExecutorService users = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < 30; i++) {
        results.add(users.submit(() ->
            api().get().uri("/weather/rome/parallel").retrieve().body(WeatherReport.class)));
      }
    }
    for (Future<WeatherReport> result : results) {
      assertThat(result.get().aqi()).isEqualTo(42);
    }
    Integer maxInFlight = RestClient.create(TestStub.url()).get()
        .uri("/stats/air-quality-max-in-flight").retrieve().body(Integer.class);
    assertThat(maxInFlight).isEqualTo(5);
    System.out.println("30 requests, max air quality calls at the same time: " + maxInFlight);
  }

  // Warms the endpoint up, then returns the fastest of three calls (the sandbox is noisy)
  private long bestOfThree(String path) {
    timeMillis(path);
    return Math.min(timeMillis(path), Math.min(timeMillis(path), timeMillis(path)));
  }

  private long timeMillis(String path) {
    long start = System.nanoTime();
    api().get().uri(path).retrieve().body(WeatherReport.class);
    return (System.nanoTime() - start) / 1_000_000;
  }
}
