package com.howtodoinjava.interceptor;

import java.io.IOException;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public final class ExchangeLoggingInterceptor implements ClientHttpRequestInterceptor {
  private static final Logger log = LoggerFactory.getLogger(ExchangeLoggingInterceptor.class);

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    long started = System.nanoTime();
    try {
      ClientHttpResponse response = execution.execute(request, body);
      log.info("HTTP {} {} -> {} in {} ms", request.getMethod(), request.getURI(),
          response.getStatusCode().value(), Duration.ofNanos(System.nanoTime() - started).toMillis());
      return response;
    } catch (IOException ex) {
      log.warn("HTTP {} {} failed after {} ms", request.getMethod(), request.getURI(),
          Duration.ofNanos(System.nanoTime() - started).toMillis(), ex);
      throw ex;
    }
  }
}
