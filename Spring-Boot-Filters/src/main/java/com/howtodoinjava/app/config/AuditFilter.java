package com.howtodoinjava.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
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
