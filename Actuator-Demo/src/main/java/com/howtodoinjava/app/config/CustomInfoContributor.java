package com.howtodoinjava.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor implements InfoContributor {

  @Autowired
  private Environment environment;

  @Override
  public void contribute(Info.Builder builder) {
    builder.withDetail("port", environment.getProperty("local.server.port"));
    builder.withDetail("profiles", environment.getDefaultProfiles());
  }
}
