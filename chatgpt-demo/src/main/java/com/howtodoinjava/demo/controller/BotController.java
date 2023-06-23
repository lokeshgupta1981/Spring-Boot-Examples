package com.howtodoinjava.demo.controller;

import com.howtodoinjava.demo.model.BotRequest;
import com.howtodoinjava.demo.model.BotResponse;
import com.howtodoinjava.demo.model.Message;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BotController {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${openai.model}")
  private String model;

  @Value("${openai.max-completions}")
  private int maxCompletions;

  @Value("${openai.temperature}")
  private double temperature;

  @Value("${openai.max_tokens}")
  private int maxTokens;

  @Value("${openai.api.url}")
  private String apiUrl;

  @PostMapping("/chat")
  public BotResponse chat(@RequestParam("prompt") String prompt) {

    BotRequest request = new BotRequest(model,
        List.of(new Message("user", prompt)),
        maxCompletions,
        temperature,
        maxTokens);

    BotResponse response = restTemplate.postForObject(apiUrl, request, BotResponse.class);
    return response;
  }
}
