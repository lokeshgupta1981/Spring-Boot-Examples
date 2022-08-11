package com.howtodoinjava.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "social-connect")
public class SocialConnectProperties {

  private Boolean debug = false;
  private OAuth oauth = new OAuth();

  public Boolean getDebug() {
    return debug;
  }

  public void setDebug(Boolean debug) {
    this.debug = debug;
  }

  public OAuth getOauth() {
    return oauth;
  }

  public void setOauth(OAuth oauth) {
    this.oauth = oauth;
  }

  public static class OAuth {

    private String consumerKey;
    private String consumerSecret;

    public String getConsumerKey() {
      return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
      this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
      return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
      this.consumerSecret = consumerSecret;
    }
  }
}
