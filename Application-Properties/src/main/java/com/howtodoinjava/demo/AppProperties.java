package com.howtodoinjava.demo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Component
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource("classpath:datasource.properties")
})
@Validated
public class AppProperties {

  @NotEmpty
  @Value("${application.name}")
  private String appName;

  @NotEmpty
  @Value("${spring.datasource.url}")
  private String datasourceUrl;

  @Value("${app.environments}")
  private String[] environments;

  @Value("#{'${app.environments}'.split(',')}")
  private List<String> environmentsList;

}
