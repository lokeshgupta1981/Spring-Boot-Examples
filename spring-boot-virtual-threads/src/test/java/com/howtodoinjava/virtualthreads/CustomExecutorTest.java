package com.howtodoinjava.virtualthreads;

import com.howtodoinjava.virtualthreads.model.WeatherReport;
import com.howtodoinjava.virtualthreads.mvc.ReportAuditService;
import com.howtodoinjava.virtualthreads.mvc.WeatherApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Any custom Executor bean switches off Spring Boot's virtual thread applicationTaskExecutor.
 * With two TaskExecutor beans (our pool and the task scheduler) @Async cannot pick one and falls back
 * to a new SimpleAsyncTaskExecutor on platform threads, even with spring.threads.virtual.enabled=true.
 */
@SpringBootTest(classes = {WeatherApplication.class, CustomExecutorTest.PoolConfig.class})
class CustomExecutorTest {

  @TestConfiguration
  static class PoolConfig {
    @Bean
    ThreadPoolTaskExecutor reportExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(4);
      executor.setThreadNamePrefix("report-");
      return executor;
    }
  }

  @Autowired
  ReportAuditService auditService;

  @Test
  void customPoolSwitchesOffVirtualAsyncExecutor() throws Exception {
    Thread thread = auditService.audit(new WeatherReport("london", 18, "cloudy", 42, "", true)).get();

    assertThat(thread.isVirtual()).isFalse();
    assertThat(thread.getName()).startsWith("SimpleAsyncTaskExecutor-");
    System.out.println("@Async thread with a custom pool: " + thread);
  }
}
