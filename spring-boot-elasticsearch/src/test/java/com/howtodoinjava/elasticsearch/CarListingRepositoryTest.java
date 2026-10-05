package com.howtodoinjava.elasticsearch;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.index.Settings;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "cars.demo.enabled=false")
@Import(ElasticsearchTestConfig.class)
class CarListingRepositoryTest {

  @Autowired
  CarListingRepository repository;

  @Autowired
  ElasticsearchOperations operations;

  @BeforeEach
  void loadListings() {
    repository.deleteAll();
    repository.saveAll(CarData.listings());
  }

  static List<String> labels(Iterable<CarListing> cars) {
    List<String> labels = new java.util.ArrayList<>();
    cars.forEach(car -> labels.add(car.label()));
    System.out.println("RESULT " + labels);
    return labels;
  }

  @Test
  @SuppressWarnings("unchecked")
  void indexIsCreatedFromTheAnnotations() {
    IndexOperations indexOps = operations.indexOps(CarListing.class);
    assertThat(indexOps.exists()).isTrue();

    Map<String, Object> mapping = indexOps.getMapping();
    System.out.println("MAPPING " + mapping);
    Map<String, Map<String, Object>> properties = (Map<String, Map<String, Object>>) mapping.get("properties");
    assertThat(properties.get("make")).containsEntry("type", "keyword");
    assertThat(properties.get("year")).containsEntry("type", "integer");
    assertThat(properties.get("description"))
        .containsEntry("type", "text")
        .containsEntry("analyzer", "english");
    assertThat(properties.get("_class")).containsEntry("type", "keyword");

    Settings settings = indexOps.getSettings();
    System.out.println("SETTINGS " + settings);
    assertThat(settings.flatten()).containsEntry("index.number_of_shards", "1")
        .containsEntry("index.number_of_replicas", "0");
  }

  @Test
  void saveAndFindById() {
    CarListing civic = repository.findById("3").orElseThrow();
    assertThat(civic.toString()).isEqualTo("Honda Civic 2018 (13500, Austin)");
    assertThat(repository.count()).isEqualTo(8);
    assertThat(repository.existsById("9")).isFalse();
  }

  @Test
  void derivedQueries() {
    assertThat(repository.findByMake("Toyota")).extracting(CarListing::label)
        .containsExactly("Toyota Corolla 2019", "Toyota Camry 2021");
    assertThat(labels(repository.findByMake("toyota"))).isEmpty();
    assertThat(labels(repository.findByMakeAndCity("Honda", "Austin")))
        .containsExactly("Honda Civic 2018");
    assertThat(labels(repository.findByPriceBetween(15000, 22000)))
        .containsExactlyInAnyOrder("Toyota Corolla 2019", "Honda Accord 2020", "Toyota Camry 2021");
    assertThat(labels(repository.findByYearGreaterThanEqualOrderByPriceAsc(2021)))
        .containsExactly("Toyota Camry 2021", "Tesla Model 3 2021", "Ford Mustang 2022");
    assertThat(repository.countByCity("Denver")).isEqualTo(3);
  }

  @Test
  void derivedQueryOnTextField() {
    assertThat(labels(repository.findByDescription("leather seat")))
        .containsExactlyInAnyOrder("Toyota Camry 2021", "Ford Mustang 2022", "BMW X3 2019");
    assertThat(labels(repository.findByDescription("hybrid")))
        .containsExactlyInAnyOrder("Toyota Camry 2021", "Honda Accord 2020");
    // every word must match: a query_string query with the AND operator
    assertThat(labels(repository.findByDescription("leather hybrid")))
        .containsExactly("Toyota Camry 2021");
  }

  @Test
  void pagingWithRepository() {
    Page<CarListing> page = repository.findByCity("Austin", PageRequest.of(0, 2, Sort.by("price")));
    assertThat(labels(page.getContent())).containsExactly("Honda Civic 2018", "Toyota Corolla 2019");
    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.getTotalPages()).isEqualTo(2);
  }

  @Test
  void queryAnnotation() {
    assertThat(labels(repository.searchDescriptionUnderPrice("leather", 30000)))
        .containsExactlyInAnyOrder("Toyota Camry 2021", "BMW X3 2019");
  }

  @Test
  void highlightAnnotation() {
    SearchHits<CarListing> hits = repository.searchByDescription("leather");
    List<String> fragments = hits.stream()
        .map(hit -> hit.getHighlightField("description").getFirst())
        .toList();
    System.out.println("RESULT " + fragments);
    assertThat(hits.getTotalHits()).isEqualTo(3);
    assertThat(fragments).contains("V8 engine, <em>leather</em> seats, like new.");
  }

  @Test
  void updateWithSaveReplacesTheDocument() {
    CarListing corolla = repository.findById("1").orElseThrow();
    corolla.setPrice(14000);
    repository.save(corolla);

    assertThat(repository.findById("1").orElseThrow().getPrice()).isEqualTo(14000);
    assertThat(labels(repository.findByPriceBetween(13000, 14500)))
        .containsExactlyInAnyOrder("Honda Civic 2018", "Toyota Corolla 2019");
  }

  @Test
  void deleting() {
    repository.deleteById("5");
    assertThat(repository.existsById("5")).isFalse();

    long deleted = repository.deleteByYearLessThan(2019);
    System.out.println("RESULT deleted " + deleted);
    assertThat(deleted).isEqualTo(1);    // Civic 2018; the Focus 2017 is already gone
    assertThat(repository.count()).isEqualTo(6);
  }
}
