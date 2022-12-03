package com.howtodoinjava.app.config;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class LoggingFilter implements Filter {

  private final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

  @Override
  public void doFilter(
      ServletRequest request,
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    log.info("Logging Filter Invoked...");
    chain.doFilter(request, response);
  }
}
