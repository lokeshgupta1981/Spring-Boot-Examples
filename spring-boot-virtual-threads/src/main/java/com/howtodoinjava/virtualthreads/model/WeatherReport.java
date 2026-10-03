package com.howtodoinjava.virtualthreads.model;

/**
 * The aggregated response. The last two fields show which thread built the report.
 */
public record WeatherReport(String city, int temperature, String sky, int aqi,
                            String thread, boolean virtual) {

  public static WeatherReport of(Forecast forecast, AirQuality airQuality) {
    Thread current = Thread.currentThread();
    return new WeatherReport(forecast.city(), forecast.temperature(), forecast.sky(),
        airQuality.aqi(), current.toString(), current.isVirtual());
  }
}
