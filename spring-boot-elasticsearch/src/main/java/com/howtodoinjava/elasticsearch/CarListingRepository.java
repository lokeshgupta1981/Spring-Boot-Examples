package com.howtodoinjava.elasticsearch;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CarListingRepository extends ElasticsearchRepository<CarListing, String> {

  List<CarListing> findByMake(String make);

  List<CarListing> findByMakeAndCity(String make, String city);

  List<CarListing> findByPriceBetween(int min, int max);

  List<CarListing> findByYearGreaterThanEqualOrderByPriceAsc(int year);

  List<CarListing> findByDescription(String text);

  Page<CarListing> findByCity(String city, Pageable pageable);

  long countByCity(String city);

  long deleteByYearLessThan(int year);

  @Query("""
      {"bool": {
        "must":   [{"match": {"description": "?0"}}],
        "filter": [{"range": {"price": {"lte": ?1}}}]
      }}""")
  List<CarListing> searchDescriptionUnderPrice(String text, int maxPrice);

  @Highlight(fields = @HighlightField(name = "description"))
  SearchHits<CarListing> searchByDescription(String text);
}
