package com.howtodoinjava.demo.repositories;

import com.howtodoinjava.demo.model.MovieDetails;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository to connect with DynamoDB and fetch daya with queries from movie_info_tbl
 */
@EnableScan
public interface MovieDetailRepository extends CrudRepository<MovieDetails, String> {

}