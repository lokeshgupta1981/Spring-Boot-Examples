package com.howtodoinjava.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "social-connect")
public class SocialConnectProperties {

  private Boolean debug = false;
  private OAuth oauth = new OAuth();

  public OAuth getOauth() {
    return oauth;
  }

  @Data
  public static class OAuth {

    private String consumerKey = null;
    private String consumerSecret = null;
  }
}
