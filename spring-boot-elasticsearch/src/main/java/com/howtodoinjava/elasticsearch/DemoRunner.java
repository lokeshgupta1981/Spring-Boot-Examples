package com.howtodoinjava.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/** Loads the sample listings and prints a few searches when the application starts. */
@Component
@ConditionalOnBooleanProperty(name = "cars.demo.enabled", matchIfMissing = true)
public class DemoRunner implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DemoRunner.class);

  private final CarListingRepository repository;
  private final CarSearchService searchService;

  public DemoRunner(CarListingRepository repository, CarSearchService searchService) {
    this.repository = repository;
    this.searchService = searchService;
  }

  @Override
  public void run(String... args) {
    repository.saveAll(CarData.listings());
    log.info("Indexed {} listings", repository.count());
    log.info("findByMake(Toyota): {}", repository.findByMake("Toyota"));
    log.info("leather under 30000 in Denver: {}",
        searchService.labels(searchService.fullTextSearch("leather", 30000, "Denver")));
    log.info("Listings per make: {}", searchService.countByMake());
    log.info("Highlights for 'leather': {}", searchService.highlight("leather"));
  }
}
