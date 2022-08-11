package com.howtodoinjava.app.config;

import com.howtodoinjava.app.service.SocialConnectService;
import com.howtodoinjava.app.service.SocialConnectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SocialConnectService.class)
@EnableConfigurationProperties(SocialConnectProperties.class)
public class SocialConnectAutoConfiguration {

  @Autowired
  private SocialConnectProperties properties;

  @Bean
  @ConditionalOnMissingBean(SocialConnectService.class)
  public SocialConnectService socialConnectService() {
    System.out.println("Building Social Connect Service");
    return new SocialConnectServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean(SocialConnection.class)
  public SocialConnection socialConnection() {
    String key = properties.getOauth().getConsumerKey();
    String secret = properties.getOauth().getConsumerSecret();
    System.out.println("ConsumerKey : " + key);
    System.out.println("ConsumerSecret : " + secret);
    return new SocialConnection().connect(key, secret);
  }
}