package com.howtodoinjava.app;

import com.howtodoinjava.app.service.SocialConnectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SocialConnectStarterSpringBootApplicationTest {

  @Autowired
  private SocialConnectService socialConnectService;

  @Test
  public void fetchMessages() {
    List<String> messages = socialConnectService.fetchMessages();
    Assertions.assertEquals(1, messages.size());
  }
}
