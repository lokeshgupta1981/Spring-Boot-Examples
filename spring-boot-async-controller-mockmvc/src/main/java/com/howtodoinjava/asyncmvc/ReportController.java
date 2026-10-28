package com.howtodoinjava.asyncmvc;
import java.util.concurrent.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
@RestController
@RequestMapping("/reports")
public class ReportController {

  private final ReportService reportService;

  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }

  // 1. CompletableFuture: the service runs the work on its own executor
  @GetMapping("/{month}")
  public CompletableFuture<Report> monthly(@PathVariable String month) {
    return reportService.monthlyReport(month);
  }

  // 2. Callable: Spring MVC runs the Callable on its task executor
  @GetMapping("/status")
  public Callable<String> status() {
    return () -> "Reports are up to date";
  }

  // 3. DeferredResult: another thread sets the result, or the timeout value is used
  @GetMapping("/{month}/deferred")
  public DeferredResult<Report> deferred(@PathVariable String month) {
    DeferredResult<Report> result = new DeferredResult<>(5_000L);
    reportService.monthlyReport(month).whenComplete((report, error) -> {
      if (error != null) {
        result.setErrorResult(error.getCause());
      } else {
        result.setResult(report);
      }
    });
    return result;
  }

  @ExceptionHandler(ReportNotFoundException.class)
  public ProblemDetail notFound(ReportNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }
}
