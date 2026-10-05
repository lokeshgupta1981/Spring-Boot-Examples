package com.howtodoinjava.virtualthreads.mvc;

import com.howtodoinjava.virtualthreads.model.AirQuality;
import com.howtodoinjava.virtualthreads.model.Forecast;
import com.howtodoinjava.virtualthreads.model.WeatherReport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class WeatherController {

  private final WeatherClient client;

  public WeatherController(WeatherClient client) {
    this.client = client;
  }

  // Two blocking calls, one after the other
  @GetMapping("/weather/{city}")
  public WeatherReport weather(@PathVariable String city) {
    Forecast forecast = client.forecast(city);
    AirQuality airQuality = client.airQuality(city);
    return WeatherReport.of(forecast, airQuality);
  }

  // The same two calls, each on its own new virtual thread
  @GetMapping("/weather/{city}/parallel")
  public WeatherReport weatherParallel(@PathVariable String city)
      throws InterruptedException, ExecutionException {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      Future<Forecast> forecast = executor.submit(() -> client.forecast(city));
      Future<AirQuality> airQuality = executor.submit(() -> client.airQuality(city));
      return WeatherReport.of(forecast.get(), airQuality.get());
    }
  }
}
