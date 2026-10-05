package com.howtodoinjava.virtualthreads.stub;

import com.howtodoinjava.virtualthreads.model.AirQuality;
import com.howtodoinjava.virtualthreads.model.Forecast;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class StubController {

  private final Duration delay;
  private final AtomicInteger airQualityInFlight = new AtomicInteger();
  private final AtomicInteger airQualityMaxInFlight = new AtomicInteger();

  public StubController(Environment env) {
    this.delay = env.getRequiredProperty("stub.delay", Duration.class);
  }

  @GetMapping("/forecast/{city}")
  public Mono<Forecast> forecast(@PathVariable String city) {
    return Mono.just(new Forecast(city, 18, "cloudy"))
        .delayElement(delay);   // answers after the configured delay without blocking a thread
  }

  @GetMapping("/air-quality/{city}")
  public Mono<AirQuality> airQuality(@PathVariable String city) {
    return Mono.just(new AirQuality(city, 42))
        .delayElement(delay)
        .doOnSubscribe(s -> airQualityMaxInFlight.accumulateAndGet(
            airQualityInFlight.incrementAndGet(), Math::max))
        .doFinally(signal -> airQualityInFlight.decrementAndGet());
  }

  // Highest number of /air-quality calls in progress at the same time (used by the tests)
  @GetMapping("/stats/air-quality-max-in-flight")
  public int airQualityMaxInFlight() {
    return airQualityMaxInFlight.get();
  }
}
