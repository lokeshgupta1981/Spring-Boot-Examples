package com.howtodoinjava.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:datasource.properties")
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceProps {
  private String url;
  private String username;
  private String password;
  private String driverClassName;
  private String dialect;
}

