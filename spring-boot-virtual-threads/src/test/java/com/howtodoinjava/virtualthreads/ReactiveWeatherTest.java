package com.howtodoinjava.virtualthreads;

import com.howtodoinjava.virtualthreads.model.WeatherReport;
import com.howtodoinjava.virtualthreads.reactive.ReactiveWeatherApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ReactiveWeatherApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.main.web-application-type=reactive")
@ActiveProfiles("reactive")
class ReactiveWeatherTest {

  @DynamicPropertySource
  static void stub(DynamicPropertyRegistry registry) {
    registry.add("weather.stub-url", TestStub::url);
  }

  @LocalServerPort
  int port;

  RestClient api() {
    return RestClient.create("http://localhost:" + port);
  }

  @Test
  void responseIsBuiltOnNettyEventLoop() {
    WeatherReport report = api().get().uri("/weather/london").retrieve().body(WeatherReport.class);

    assertThat(report.virtual()).isFalse();
    assertThat(report.thread()).containsPattern("(reactor|webflux)-http-");
    assertThat(report.aqi()).isEqualTo(42);
    System.out.println("WebFlux thread: " + report.thread());
  }

  @Test
  void zipRunsBothCallsAtTheSameTime() {
    long sequential = bestOfThree("/weather/paris");
    long parallel = bestOfThree("/weather/paris/parallel");

    assertThat(sequential).isGreaterThanOrEqualTo(2 * TestStub.DELAY_MS);
    assertThat(parallel).isGreaterThanOrEqualTo(TestStub.DELAY_MS).isLessThan(2 * TestStub.DELAY_MS);
    System.out.println("WebFlux sequential " + sequential + " ms, parallel " + parallel + " ms");
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
