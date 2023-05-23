package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.entity.MovieDetails;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Service
public class DynamoDbOperationService {

  @Autowired
  private DynamoDbTemplate dynamoDbTemplate;

  @Autowired
  private DynamoDbClient dynamoDbClient;

  @Autowired
  private DynamoDbEnhancedClient dynamoDbEnhancedClient;

  private DynamoDbTable<MovieDetails> movieTable;

  public MovieDetails saveData(MovieDetails movieDetails) {

    return dynamoDbTemplate.save(movieDetails);
  }

  public MovieDetails updateData(MovieDetails movieDetails) {

    return dynamoDbTemplate.update(movieDetails);
  }

  public void deleteByObject(MovieDetails movieDetails) {

    dynamoDbTemplate.delete(movieDetails);
  }

  public void deleteById(String id) {

    Key key = Key.builder().partitionValue(id).build();
    dynamoDbTemplate.delete(key, MovieDetails.class);
  }

  public MovieDetails findById(String hashKey) {

    Key key = Key.builder().partitionValue(hashKey).build();
    return dynamoDbTemplate.load(key, MovieDetails.class);
  }

  public List<MovieDetails> scanDataByGenre(String genre) {
    Map<String, AttributeValue> expressionValues = new HashMap<>();
    expressionValues.put(":val1", AttributeValue.fromS(genre));

    Expression filterExpression = Expression.builder()
        .expression("genre = :val1")
        .expressionValues(expressionValues)
        .build();

    ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
        .filterExpression(filterExpression).build();

    PageIterable<MovieDetails> returnedList = dynamoDbTemplate.scan(scanEnhancedRequest,
        MovieDetails.class);

    return returnedList.items().stream().collect(Collectors.toList());
  }

  public PageIterable<MovieDetails> queryData(String partitionKey, String genre) {

    Map<String, AttributeValue> expressionValues = new HashMap<>();
    expressionValues.put(":value", AttributeValue.fromS(genre));

    Expression filterExpression = Expression.builder()
        .expression("genre = :val1")
        .expressionValues(expressionValues)
        .build();

    QueryConditional queryConditional = QueryConditional
        .keyEqualTo(
            Key.builder()
                .partitionValue(partitionKey)
                .build());

    QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
        .queryConditional(queryConditional)
        .filterExpression(filterExpression)
        .build();

    return dynamoDbTemplate.query(queryRequest, MovieDetails.class);
  }
}
