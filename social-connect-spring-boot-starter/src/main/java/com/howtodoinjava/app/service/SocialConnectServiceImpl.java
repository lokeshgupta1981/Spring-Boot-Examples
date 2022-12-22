package com.howtodoinjava.app.service;

import com.howtodoinjava.app.config.SocialConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class SocialConnectServiceImpl implements SocialConnectService {

  @Autowired
  private SocialConnection socialConnection;

  @Override
  public List<String> fetchMessages() {
    log.info("Fetching messages using SocialConnection ...");
    //TODO: Do something with socialConnection
    return List.of("Hello, World!");
  }
}