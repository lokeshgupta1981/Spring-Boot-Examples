package com.howtodoinjava.elasticsearch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

@Service
public class CarSearchService {

  private final ElasticsearchOperations operations;

  public CarSearchService(ElasticsearchOperations operations) {
    this.operations = operations;
  }

  // 1. Index and get
  public CarListing save(CarListing car) {
    return operations.save(car);
  }

  public CarListing get(String id) {
    return operations.get(id, CarListing.class);
  }

  // 2. CriteriaQuery: Spring Data's own query API
  public SearchHits<CarListing> cheaperThan(String make, int maxPrice) {
    Criteria criteria = new Criteria("make").is(make)
        .and("price").lessThan(maxPrice);
    return operations.search(new CriteriaQuery(criteria), CarListing.class);
  }

  // 3. NativeQuery: full Elasticsearch query DSL through the Java client builders
  public SearchHits<CarListing> fullTextSearch(String text, int maxPrice, String city) {
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.bool(b -> b
            .must(m -> m.match(t -> t.field("description").query(text)))
            .filter(f -> f.range(r -> r.number(n -> n.field("price").lte((double) maxPrice))))
            .filter(f -> f.term(t -> t.field("city").value(city)))))
        .build();
    return operations.search(query, CarListing.class);
  }

  public SearchHits<CarListing> matchAllWords(String text) {
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.match(m -> m.field("description").query(text)
            .operator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.And)))
        .build();
    return operations.search(query, CarListing.class);
  }

  public SearchHits<CarListing> fullTextSearchAnyWord(String text) {
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.match(m -> m.field("description").query(text)))
        .build();
    return operations.search(query, CarListing.class);
  }

  public SearchHits<CarListing> termOnDescription(String value) {
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.term(t -> t.field("description").value(value)))
        .build();
    return operations.search(query, CarListing.class);
  }

  public SearchHits<CarListing> fuzzySearch(String text) {
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.match(m -> m.field("description").query(text).fuzziness("AUTO")))
        .build();
    return operations.search(query, CarListing.class);
  }

  // 4. Partial update and delete
  public UpdateResponse.Result updatePrice(String id, int newPrice) {
    UpdateQuery update = UpdateQuery.builder(id)
        .withDocument(Document.create().append("price", newPrice))
        .build();
    return operations.update(update, operations.getIndexCoordinatesFor(CarListing.class)).getResult();
  }

  public String delete(String id) {
    return operations.delete(id, CarListing.class);
  }

  public long deleteByCity(String city) {
    CriteriaQuery query = new CriteriaQuery(new Criteria("city").is(city));
    ByQueryResponse response = operations.delete(DeleteQuery.builder(query).build(), CarListing.class);
    return response.getDeleted();
  }

  // 5. Aggregations
  public Map<String, Long> countByMake() {
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.matchAll(m -> m))
        .withAggregation("by_make", Aggregation.of(a -> a.terms(t -> t.field("make"))))
        .withMaxResults(0)
        .build();
    SearchHits<CarListing> hits = operations.search(query, CarListing.class);

    ElasticsearchAggregations aggregations = (ElasticsearchAggregations) hits.getAggregations();
    Aggregate byMake = aggregations.get("by_make").aggregation().getAggregate();

    Map<String, Long> counts = new LinkedHashMap<>();
    for (StringTermsBucket bucket : byMake.sterms().buckets().array()) {
      counts.put(bucket.key().stringValue(), bucket.docCount());
    }
    return counts;
  }

  public Map<String, Double> averagePriceByCity() {
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.matchAll(m -> m))
        .withAggregation("by_city", Aggregation.of(a -> a
            .terms(t -> t.field("city"))
            .aggregations("avg_price", sub -> sub.avg(avg -> avg.field("price")))))
        .withMaxResults(0)
        .build();
    SearchHits<CarListing> hits = operations.search(query, CarListing.class);

    ElasticsearchAggregations aggregations = (ElasticsearchAggregations) hits.getAggregations();
    Aggregate byCity = aggregations.get("by_city").aggregation().getAggregate();

    Map<String, Double> averages = new LinkedHashMap<>();
    for (StringTermsBucket bucket : byCity.sterms().buckets().array()) {
      averages.put(bucket.key().stringValue(), bucket.aggregations().get("avg_price").avg().value());
    }
    return averages;
  }

  // 6. Highlighting
  public List<String> highlight(String text) {
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.match(m -> m.field("description").query(text)))
        .withHighlightQuery(new HighlightQuery(
            new Highlight(List.of(new HighlightField("description"))), CarListing.class))
        .build();
    return operations.search(query, CarListing.class).stream()
        .map(hit -> hit.getHighlightField("description").getFirst())
        .toList();
  }

  // 7. Pagination and sorting
  public SearchPage<CarListing> page(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("price").descending());
    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q.matchAll(m -> m))
        .withPageable(pageable)
        .build();
    SearchHits<CarListing> hits = operations.search(query, CarListing.class);
    return SearchHitSupport.searchPageFor(hits, pageable);
  }

  public List<String> labels(SearchHits<CarListing> hits) {
    return hits.stream().map(SearchHit::getContent).map(CarListing::label).toList();
  }
}
