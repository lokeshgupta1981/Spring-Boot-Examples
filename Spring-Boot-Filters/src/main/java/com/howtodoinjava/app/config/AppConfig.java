package com.howtodoinjava.app.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Bean
  public FilterRegistrationBean<SecurityFilter> filterRegistrationBean() {
    FilterRegistrationBean<SecurityFilter> registrationBean =
        new FilterRegistrationBean();

    registrationBean.setFilter(new SecurityFilter());
    registrationBean.addUrlPatterns("/admin/*");
    registrationBean.setOrder(2);
    return registrationBean;
  }
}
