package com.howtodoinjava.virtualthreads.mvc;

import com.howtodoinjava.virtualthreads.model.WeatherReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ReportAuditService {

  private static final Logger log = LoggerFactory.getLogger(ReportAuditService.class);

  // Runs on the applicationTaskExecutor; returns the thread for the demo and tests
  @Async
  public CompletableFuture<Thread> audit(WeatherReport report) {
    log.info("Audited report for {} on {}", report.city(), Thread.currentThread());
    return CompletableFuture.completedFuture(Thread.currentThread());
  }
}
