package com.howtodoinjava.library.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * A fake shelf scanner: the first two count() calls fail, the third one returns 3.
 * reserve() takes 200 ms and records how many threads run it at the same time.
 */
public class ShelfScanner {

  private final AtomicInteger countCalls = new AtomicInteger();
  private final AtomicInteger running = new AtomicInteger();
  private final AtomicInteger maxRunning = new AtomicInteger();

  public int count(String title) {
    if (countCalls.incrementAndGet() < 3) {
      throw new ShelfBusyException("Scanner busy for " + title);
    }
    return 3;
  }

  public String reserve(String title) {
    int now = running.incrementAndGet();
    maxRunning.accumulateAndGet(now, Math::max);
    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(200));
    running.decrementAndGet();
    return "reserved " + title;
  }

  public int countCalls() {
    return countCalls.get();
  }

  public int maxRunning() {
    return maxRunning.get();
  }
}
