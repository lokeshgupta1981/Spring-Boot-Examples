package com.howtodoinjava.demo.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

@Configuration
@EnableDynamoDBRepositories(
    basePackages = {"com.howtodoinjava.demo.repositories"})
public class DynamoDbConfiguration {

  @Value("${aws.dynamodb.endpoint}")
  private String dynamoDbEndpoint;

  @Value("${aws.accessKey}")
  private String awsAccessKey;

  @Value("${aws.secretKey}")
  private String awsSecretKey;

  private AWSCredentialsProvider awsDynamoDBCredentials() {
    return new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(awsAccessKey, awsSecretKey));
  }

  @Primary
  @Bean
  public DynamoDBMapperConfig dynamoDBMapperConfig() {
    return DynamoDBMapperConfig.DEFAULT;
  }

  @Bean
  @Primary
  public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB,
                                       DynamoDBMapperConfig config) {
    return new DynamoDBMapper(amazonDynamoDB, config);
  }

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    AmazonDynamoDB amazonDynamoDB =
        new AmazonDynamoDBClient(awsDynamoDBCredentials());
    if (!StringUtils.isEmpty(dynamoDbEndpoint)) {
      amazonDynamoDB.setEndpoint(dynamoDbEndpoint);
    }
    return amazonDynamoDB;
  }
}
