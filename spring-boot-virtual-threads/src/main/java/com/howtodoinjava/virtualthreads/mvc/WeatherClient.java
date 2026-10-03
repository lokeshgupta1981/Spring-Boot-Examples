package com.howtodoinjava.virtualthreads.mvc;

import com.howtodoinjava.virtualthreads.model.AirQuality;
import com.howtodoinjava.virtualthreads.model.Forecast;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Semaphore;

@Component
public class WeatherClient {

  private final RestClient restClient;
  private final Semaphore airQualityPermits;

  public WeatherClient(RestClient.Builder builder, WeatherProperties properties) {
    this.restClient = builder.baseUrl(properties.stubUrl()).build();
    this.airQualityPermits = new Semaphore(properties.airQualityLimit());
  }

  public Forecast forecast(String city) {
    return restClient.get()
        .uri("/forecast/{city}", city)
        .retrieve()
        .body(Forecast.class);
  }

  public AirQuality airQuality(String city) {
    airQualityPermits.acquireUninterruptibly();   // waits when the limit is reached
    try {
      return restClient.get()
          .uri("/air-quality/{city}", city)
          .retrieve()
          .body(AirQuality.class);
    } finally {
      airQualityPermits.release();
    }
  }
}
