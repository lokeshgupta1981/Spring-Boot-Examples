package com.howtodoinjava.library.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Framework 7 without Spring Boot.
 */
class PlainSpringContextTest {

  @Test
  void retryableRetriesTwiceThenReturnsCopies() {
    try (var context = new AnnotationConfigApplicationContext(LibraryConfig.class)) {
      ShelfClient shelf = context.getBean(ShelfClient.class);
      int copies = shelf.copies("dune");          // 3, after 2 failed calls retried by @Retryable

      assertThat(copies).isEqualTo(3);
      assertThat(context.getBean(ShelfScanner.class).countCalls()).isEqualTo(3);
      System.out.println("copies = " + copies + ", scanner calls = "
          + context.getBean(ShelfScanner.class).countCalls());
    }
  }

  @Test
  void concurrencyLimitAllowsTwoCallsAtATime() throws Exception {
    try (var context = new AnnotationConfigApplicationContext(LibraryConfig.class);
         ExecutorService pool = Executors.newFixedThreadPool(5)) {
      ShelfClient shelf = context.getBean(ShelfClient.class);
      List<Future<String>> results = pool.invokeAll(List.of(
          () -> shelf.reserve("dune"), () -> shelf.reserve("emma"), () -> shelf.reserve("dune"),
          () -> shelf.reserve("emma"), () -> shelf.reserve("dune")));
      for (Future<String> result : results) {
        assertThat(result.get()).startsWith("reserved");
      }
      int maxRunning = context.getBean(ShelfScanner.class).maxRunning();
      System.out.println("5 callers, max running at once = " + maxRunning);
      assertThat(maxRunning).isEqualTo(2);
    }
  }

  @Test
  void beanRegistrarRegistersOneDeskPerBranch() {
    try (var context = new AnnotationConfigApplicationContext()) {
      context.getEnvironment().getPropertySources()
          .addFirst(new MapPropertySource("test", Map.of("library.branches", "north,south")));
      context.register(LibraryConfig.class);
      context.refresh();

      Map<String, HelpDesk> desks = context.getBeansOfType(HelpDesk.class);
      System.out.println("desks = " + desks);
      assertThat(desks).containsOnlyKeys("northDesk", "southDesk");
      assertThat(desks.get("northDesk").branch()).isEqualTo("north");
    }
  }

  @Test
  void beanRegistrarWithoutBranchesRegistersNoDesk() {
    try (var context = new AnnotationConfigApplicationContext(LibraryConfig.class)) {
      assertThat(context.getBeansOfType(HelpDesk.class)).isEmpty();
    }
  }
}
