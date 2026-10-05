package com.howtodoinjava.sqs;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stands in for a slow email service. Each call takes about 300 ms, and the class
 * counts how many calls run at the same time so we can see the listener concurrency.
 */
@Component
public class EmailSender {

  private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

  private final AtomicInteger inFlight = new AtomicInteger();
  private final AtomicInteger maxInFlight = new AtomicInteger();

  public void send(InvoiceCreated invoice) {
    int running = inFlight.incrementAndGet();
    maxInFlight.accumulateAndGet(running, Math::max);
    try {
      Thread.sleep(300);
      log.info("Emailed invoice {} to {}", invoice.number(), invoice.customer());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      inFlight.decrementAndGet();
    }
  }

  public int maxInFlight() {
    return maxInFlight.get();
  }
}
