package com.howtodoinjava.elasticsearch;

import java.util.List;
import java.util.Map;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "cars.demo.enabled=false")
@Import(ElasticsearchTestConfig.class)
class CarSearchServiceTest {

  @Autowired
  CarSearchService service;

  @Autowired
  CarListingRepository repository;

  @Autowired
  ElasticsearchOperations operations;

  @Autowired
  ElasticsearchClient client;

  @BeforeEach
  void loadListings() {
    repository.deleteAll();
    repository.saveAll(CarData.listings());
  }

  static <T> T print(T value) {
    System.out.println("RESULT " + value);
    return value;
  }

  @Test
  void saveAndGetWithOperations() {
    CarListing saved = service.save(
        new CarListing("9", "Kia", "Rio", 2020, 11000, 50000, "Cheap to run, new battery.", "Austin"));
    assertThat(saved.getId()).isEqualTo("9");
    assertThat(print(service.get("9")).toString()).isEqualTo("Kia Rio 2020 (11000, Austin)");
    assertThat(service.get("99")).isNull();
  }

  @Test
  void searchRightAfterSaveNeedsARefresh() {
    CriteriaQuery kia = new CriteriaQuery(new Criteria("make").is("Kia"));

    operations.save(new CarListing("9", "Kia", "Rio", 2020, 11000, 50000, "Cheap to run.", "Austin"));
    long beforeRefresh = operations.count(kia, CarListing.class);
    operations.indexOps(CarListing.class).refresh();
    long afterRefresh = operations.count(kia, CarListing.class);

    operations.withRefreshPolicy(RefreshPolicy.IMMEDIATE)
        .save(new CarListing("10", "Kia", "Soul", 2021, 14000, 30000, "Roomy.", "Denver"));
    long withImmediate = operations.count(kia, CarListing.class);

    print(List.of(beforeRefresh, afterRefresh, withImmediate));
    assertThat(beforeRefresh).isZero();
    assertThat(afterRefresh).isEqualTo(1);
    assertThat(withImmediate).isEqualTo(2);
  }

  @Test
  void criteriaQuery() {
    assertThat(print(service.labels(service.cheaperThan("Toyota", 20000))))
        .containsExactly("Toyota Corolla 2019");
  }

  @Test
  void nativeBoolQuery() {
    SearchHits<CarListing> hits = service.fullTextSearch("leather", 30000, "Denver");
    assertThat(print(service.labels(hits))).containsExactlyInAnyOrder("Toyota Camry 2021", "BMW X3 2019");
    assertThat(hits.getTotalHits()).isEqualTo(2);
  }

  @Test
  void fullTextMatching() {
    // the english analyzer stems "seated" and "seats" to "seat"
    assertThat(print(service.labels(service.matchAllWords("heated seats"))))
        .containsExactly("BMW X3 2019");
    // "or" is the default operator of match
    assertThat(print(service.labels(service.fullTextSearchAnyWord("hybrid electric"))))
        .containsExactlyInAnyOrder("Toyota Camry 2021", "Honda Accord 2020", "Tesla Model 3 2021");
    // fuzziness AUTO allows one typo in a five-letter word
    assertThat(print(service.labels(service.fuzzySearch("hybrd"))))
        .containsExactlyInAnyOrder("Toyota Camry 2021", "Honda Accord 2020");
  }

  @Test
  void relevanceScore() {
    SearchHits<CarListing> hits = service.fullTextSearchAnyWord("leather seats");
    hits.forEach(hit -> System.out.println("SCORE " + hit.getContent().label() + " " + hit.getScore()));
    assertThat(hits.getSearchHit(0).getScore()).isGreaterThan(0f);
  }

  @Test
  void partialUpdate() {
    UpdateResponse.Result result = service.updatePrice("1", 14000);
    CarListing corolla = service.get("1");
    print(result + " " + corolla);
    assertThat(result).isEqualTo(UpdateResponse.Result.UPDATED);
    assertThat(corolla.toString()).isEqualTo("Toyota Corolla 2019 (14000, Austin)");
    assertThat(corolla.getDescription()).isEqualTo("One owner, full service history, new tires.");
  }

  @Test
  void deleteByIdAndByQuery() {
    assertThat(print(service.delete("5"))).isEqualTo("5");
    long deleted = service.deleteByCity("Seattle");
    print("deleted " + deleted);
    assertThat(deleted).isEqualTo(2);
    operations.indexOps(CarListing.class).refresh();
    assertThat(repository.count()).isEqualTo(5);
  }

  @Test
  void termsAggregation() {
    Map<String, Long> counts = print(service.countByMake());
    assertThat(counts).containsExactly(
        Map.entry("Ford", 2L), Map.entry("Honda", 2L), Map.entry("Toyota", 2L),
        Map.entry("BMW", 1L), Map.entry("Tesla", 1L));
  }

  @Test
  void nestedAverageAggregation() {
    Map<String, Double> averages = print(service.averagePriceByCity());
    assertThat(averages.keySet()).containsExactly("Austin", "Denver", "Seattle");
    assertThat(averages.get("Austin")).isCloseTo(20833.33, org.assertj.core.data.Offset.offset(0.01));
    assertThat(averages.get("Denver")).isEqualTo(19000.0);
    assertThat(averages.get("Seattle")).isEqualTo(23250.0);
  }

  @Test
  void highlighting() {
    List<String> fragments = print(service.highlight("leather"));
    assertThat(fragments).containsExactlyInAnyOrder(
        "Hybrid with <em>leather</em> seats and a backup camera.",
        "V8 engine, <em>leather</em> seats, like new.",
        "All wheel drive with heated <em>leather</em> seats.");
  }

  @Test
  void pagination() {
    SearchPage<CarListing> page = service.page(1, 3);
    List<String> labels = page.getContent().stream().map(hit -> hit.getContent().label()).toList();
    print(labels + " total=" + page.getTotalElements() + " pages=" + page.getTotalPages());
    assertThat(labels).containsExactly("Toyota Camry 2021", "Honda Accord 2020", "Toyota Corolla 2019");
    assertThat(page.getTotalElements()).isEqualTo(8);
    assertThat(page.getTotalPages()).isEqualTo(3);
  }

  @Test
  void termQueryOnTextField() {
    assertThat(print(service.labels(service.termOnDescription("Leather")))).isEmpty();
    assertThat(print(service.labels(service.termOnDescription("leather")))).hasSize(3);
  }

  @Test
  void javaClientDirectly() throws Exception {
    SearchResponse<CarListing> response = client.search(s -> s
            .index("car-listings")
            .query(q -> q.term(t -> t.field("make").value("Tesla"))),
        CarListing.class);
    List<String> labels = response.hits().hits().stream().map(Hit::source).map(CarListing::label).toList();
    print(labels + " took=" + response.took());
    assertThat(labels).containsExactly("Tesla Model 3 2021");
  }
}
