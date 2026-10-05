package com.howtodoinjava.virtualthreads.reactive;

import com.howtodoinjava.virtualthreads.model.AirQuality;
import com.howtodoinjava.virtualthreads.model.Forecast;
import com.howtodoinjava.virtualthreads.model.WeatherReport;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class ReactiveWeatherController {

  private final WebClient webClient;

  public ReactiveWeatherController(WebClient.Builder builder, Environment env) {
    this.webClient = builder.baseUrl(env.getRequiredProperty("weather.stub-url")).build();
  }

  // Two calls, one after the other
  @GetMapping("/weather/{city}")
  public Mono<WeatherReport> weather(@PathVariable String city) {
    return forecast(city)
        .flatMap(forecast -> airQuality(city)
            .map(airQuality -> WeatherReport.of(forecast, airQuality)));
  }

  // Both calls at the same time
  @GetMapping("/weather/{city}/parallel")
  public Mono<WeatherReport> weatherParallel(@PathVariable String city) {
    return Mono.zip(forecast(city), airQuality(city), WeatherReport::of);
  }

  private Mono<Forecast> forecast(String city) {
    return webClient.get().uri("/forecast/{city}", city).retrieve().bodyToMono(Forecast.class);
  }

  private Mono<AirQuality> airQuality(String city) {
    return webClient.get().uri("/air-quality/{city}", city).retrieve().bodyToMono(AirQuality.class);
  }
}
