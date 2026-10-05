package com.howtodoinjava.recipes;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Downstream service behind the gateway. It knows nothing about the gateway.
 */
@RestController
public class RecipeController {

  private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

  private static final Map<String, Integer> MINUTES = Map.of(
      "pancakes", 20,
      "omelette", 10,
      "lasagna", 90);

  private final AtomicInteger flakyCalls = new AtomicInteger();

  @GetMapping("/recipes/{name}")
  public ResponseEntity<Map<String, Object>> recipe(
      @PathVariable String name,
      @RequestHeader(name = "X-Request-Source", required = false) String source) {

    log.info("GET /recipes/{} (X-Request-Source={})", name, source);
    Integer minutes = MINUTES.get(name);
    if (minutes == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(Map.of(
        "name", name,
        "minutes", minutes,
        "requestSource", source == null ? "none" : source));
  }

  /** Fails with 503 on two out of every three calls. */
  @GetMapping("/flaky/recipes")
  public ResponseEntity<Map<String, Object>> flaky() {
    int call = flakyCalls.incrementAndGet();
    if (call % 3 != 0) {
      log.info("GET /flaky/recipes call {} -> 503", call);
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
    log.info("GET /flaky/recipes call {} -> 200", call);
    return ResponseEntity.ok(Map.of("name", "pancakes", "call", call));
  }

  /** Answers after 3 seconds. */
  @GetMapping("/slow/recipes")
  public Map<String, Object> slow() throws InterruptedException {
    log.info("GET /slow/recipes (waits 3 s)");
    Thread.sleep(Duration.ofSeconds(3));
    return Map.of("name", "lasagna", "minutes", 90);
  }
}
