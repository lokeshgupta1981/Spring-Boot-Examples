package com.howtodoinjava.demo.config;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDBConfiguration {

  @Value("${aws.dynamodb.accesskey}")
  private String accessKey;

  @Value("${aws.dynamodb.secretkey}")
  private String secretKey;

  @Value("${aws.dynamodb.endpoint}")
  private String endpoint;

  @Bean
  public DynamoDbClient dynamoDbClient() {
    return DynamoDbClient.builder()
        .endpointOverride(URI.create(endpoint))
        .region(Region.AP_SOUTH_1)
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  @Bean
  public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
    return DynamoDbEnhancedClient.builder()
        .dynamoDbClient(dynamoDbClient())
        .build();
  }

  @Bean
  public DynamoDbTemplate dynamoDbTemplate() {
    return new DynamoDbTemplate(dynamoDbEnhancedClient());
  }
}