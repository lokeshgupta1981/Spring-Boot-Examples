package com.howtodoinjava.asyncmvc;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
@WebMvcTest(ReportController.class)
@Import(ReportService.class)
class ReportControllerTesterTest {

  @Autowired
  MockMvcTester mvc;

  @Test
  void exchangeWaitsForTheAsyncResult() {
    assertThat(mvc.get().uri("/reports/2026-09"))
        .hasStatusOk()
        .bodyJson().isLenientlyEqualTo("{\"month\":\"2026-09\",\"orders\":95}");
  }

  @Test
  void exchangeWithTimeout() {
    assertThat(mvc.get().uri("/reports/2026-08/deferred").exchange(Duration.ofSeconds(2)))
        .hasStatusOk()
        .bodyJson().extractingPath("$.orders").isEqualTo(120);
  }

  @Test
  void asyncErrorIsHandled() {
    assertThat(mvc.get().uri("/reports/2026-01"))
        .hasStatus(HttpStatus.NOT_FOUND)
        .bodyJson().extractingPath("$.detail").isEqualTo("No report for 2026-01");
  }
}
