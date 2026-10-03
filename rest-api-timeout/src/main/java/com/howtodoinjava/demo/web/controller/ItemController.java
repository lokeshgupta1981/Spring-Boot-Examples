package com.howtodoinjava.demo.web.controller;

import com.howtodoinjava.demo.web.service.ItemService;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@SuppressWarnings("all")
public class ItemController {

  @Autowired
  ItemService itemService;

  @GetMapping
  public Callable<ResponseEntity<?>> getAll() {

    return () -> {
      try {
        return ResponseEntity.ok(itemService.getAll());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(e.getMessage());
      }
    };
  }

  @GetMapping("timeLimiter")
  @TimeLimiter(name = "itemService")
  public CompletableFuture<ResponseEntity<?>> getAllWithTimeLimiter() {

    return CompletableFuture.supplyAsync(() -> {
      try {
        return ResponseEntity.ok(itemService.getAll());
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(e.getMessage());
      }
    });
  }
}
