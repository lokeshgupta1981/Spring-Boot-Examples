package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.entity.MovieDetails;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DynamoDbOperationService {

    private DynamoDbTemplate dynamoDbTemplate;
    private DynamoDbTable<MovieDetails> movieTable;
    private DynamoDbClient dynamoDbClient;
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Autowired
    public DynamoDbOperationService(@Qualifier("dynamoDbClient") DynamoDbClient dynamoDbClient,
                                 @Qualifier("dynamoDbEnhancedClient") DynamoDbEnhancedClient dynamoDbEnhancedClient) {

        this.dynamoDbClient = dynamoDbClient;
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;

        dynamoDbTemplate = new DynamoDbTemplate(this.dynamoDbEnhancedClient);
        movieTable = DynamoDbEnhancedClient.builder().dynamoDbClient(this.dynamoDbClient).build().table("movie_details",
                TableSchema.fromBean(MovieDetails.class));

    }

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

        PageIterable<MovieDetails> returnedList = dynamoDbTemplate.scan(scanEnhancedRequest, MovieDetails.class);

        return returnedList.items().stream().collect(Collectors.toList());
    }

    public PageIterable<MovieDetails> queryData(String partitionKey,  String genre) {

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

        return dynamoDbTemplate.query(queryRequest,MovieDetails.class);
    }

}
