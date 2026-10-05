package com.howtodoinjava.library.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void configureApiVersioning(ApiVersionConfigurer configurer) {
    configurer.useQueryParam("version").setDefaultVersion("1");     // ?version=2, or 1 when missing
  }

  @Override
  public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
    JsonMapper jsonMapper = JsonMapper.builder()
        .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
        .build();
    builder.withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper));
  }
  // GET /books/emma?version=2 -> {"title":"emma","copies":0}   (no "shelf":null)
}
