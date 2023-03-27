package com.howtodoinjava.demo.repositories;

import com.howtodoinjava.demo.model.MovieDetail;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository to connect with DynamoDB and fetch daya with queries from
 * tbl_movie_dtl
 */
@EnableScan
public interface MovieDetailRepository
    extends CrudRepository<MovieDetail, String> {

}