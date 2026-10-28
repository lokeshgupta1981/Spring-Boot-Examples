package com.howtodoinjava.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/** Diagnostic use only with a buffering request factory and non-sensitive, bounded payloads. */
public final class BodyLoggingInterceptor implements ClientHttpRequestInterceptor {
  private static final Logger log = LoggerFactory.getLogger(BodyLoggingInterceptor.class);

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    ClientHttpResponse response = execution.execute(request, body);
    if (log.isDebugEnabled()) {
      log.debug("Response body: {}", new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
    }
    return response;
  }
}
