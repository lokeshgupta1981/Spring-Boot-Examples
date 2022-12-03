package com.howtodoinjava.app.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@WebFilter(urlPatterns = "/admin/*")
@Order(3)
public class AuditFilter implements Filter {
  private final Logger log = LoggerFactory.getLogger(AuditFilter.class);

  @Override
  public void doFilter(
      ServletRequest request,
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    log.info("Audit Filter Invoked...");
    chain.doFilter(request, response);
  }
}
