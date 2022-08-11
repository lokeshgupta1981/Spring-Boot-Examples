package com.howtodoinjava.app.service;

import com.howtodoinjava.app.config.SocialConnection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SocialConnectServiceImpl implements SocialConnectService {

  @Autowired
  private SocialConnection socialConnection;

  @Override
  public List<String> fetchMessages() {
    System.out.println("Fetching messages using SocialConnection ...");
    //Do something with socialConnection
    return List.of("Hello, World!");
  }
}