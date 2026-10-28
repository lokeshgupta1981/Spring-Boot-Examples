package com.howtodoinjava.asyncmvc;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
@Service
public class ReportService {

  private static final Map<String, Integer> ORDERS = Map.of("2026-08", 120, "2026-09", 95);

  private final Executor slowExecutor;

  public ReportService(TaskExecutor taskExecutor) {
    // Runs each task after 200 ms, so the report takes some time like a real database query
    this.slowExecutor = CompletableFuture.delayedExecutor(200, TimeUnit.MILLISECONDS, taskExecutor);
  }

  public CompletableFuture<Report> monthlyReport(String month) {
    return CompletableFuture.supplyAsync(() -> {
      Integer orders = ORDERS.get(month);
      if (orders == null) {
        throw new ReportNotFoundException(month);
      }
      return new Report(month, orders);
    }, slowExecutor);
  }
}
