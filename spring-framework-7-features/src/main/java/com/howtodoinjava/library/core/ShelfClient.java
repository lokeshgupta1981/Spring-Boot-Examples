package com.howtodoinjava.library.core;

import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;

public class ShelfClient {

  private final ShelfScanner scanner;

  public ShelfClient(ShelfScanner scanner) {
    this.scanner = scanner;
  }

  @Retryable(includes = ShelfBusyException.class, maxRetries = 2, delay = 100, multiplier = 2)
  public int copies(String title) {
    return scanner.count(title);              // ShelfBusyException twice, then 3
  }

  @ConcurrencyLimit(2)
  public String reserve(String title) {
    return scanner.reserve(title);            // 5 callers, at most 2 run at the same time
  }
}
