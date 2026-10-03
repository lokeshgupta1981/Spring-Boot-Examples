package com.howtodoinjava.virtualthreads.mvc;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * weather.stub-url: base URL of the downstream stub server.
 * weather.air-quality-limit: how many air quality calls may run at the same time.
 */
@ConfigurationProperties("weather")
public record WeatherProperties(String stubUrl, int airQualityLimit) {
}
