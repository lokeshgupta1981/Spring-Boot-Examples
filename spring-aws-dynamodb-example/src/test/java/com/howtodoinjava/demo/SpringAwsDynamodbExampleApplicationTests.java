package com.howtodoinjava.demo;

import com.howtodoinjava.demo.entity.MovieDetails;
import com.howtodoinjava.demo.service.DynamoDbOperationService;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class SpringAwsDynamodbExampleApplicationTests {

    @Autowired
    private DynamoDbOperationService dbOperationService;

    @Test
    void testCrud() {
        MovieDetails movieDetails = new MovieDetails("MOV001", "Avengers", null, "Action", "US", "175", "English");
        MovieDetails savedMovie =  dbOperationService.saveData(movieDetails);

        Assertions.assertEquals(movieDetails.getId(), savedMovie.getId());

        savedMovie.setTitle("Avengers Endgame");
        MovieDetails updatedMovie = dbOperationService.updateData(movieDetails);

        Assertions.assertEquals(movieDetails.getId(), updatedMovie.getId());
        Assertions.assertEquals(savedMovie.getTitle(), updatedMovie.getTitle());

        dbOperationService.deleteByObject(updatedMovie);

        MovieDetails fetchedDetails = dbOperationService.findById(movieDetails.getId());

        Assertions.assertNull(fetchedDetails);
    }

    @Test
    void testScan() throws InterruptedException {

        MovieDetails actionMovie = new MovieDetails("MOV001", "Avengers", null,
                "Action", "US", "175", "English");
        dbOperationService.saveData(actionMovie);

        MovieDetails ThrillerMovie = new MovieDetails("MOV002", "James Bond", null,
                "Thriller", "US", "167", "English");
        dbOperationService.saveData(ThrillerMovie);


        List<MovieDetails> fetchedDataList = dbOperationService.scanDataByGenre("Thriller");
        Long countResult = fetchedDataList.stream().count();

        Assertions.assertEquals(1, countResult);

        List<MovieDetails> fetchedActionDataList = dbOperationService.scanDataByGenre("Action");
        Long countActionResult = fetchedActionDataList.stream().count();

        Assertions.assertEquals(1, countActionResult);

        // cleanUp(dynamoDbTable, actionMovie.getId());
        dbOperationService.deleteByObject(actionMovie);
        dbOperationService.deleteByObject(ThrillerMovie);

    }

    private static void describeAndCreateTable(DynamoDbClient dynamoDbClient, @Nullable String tablePrefix) {
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("id").attributeType("S").build());

        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
        tableKeySchema.add(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build());

        String tableName = StringUtils.hasText(tablePrefix) ? tablePrefix.concat("movie_details") : "movie_details";
        CreateTableRequest createTableRequest = CreateTableRequest.builder().tableName(tableName)
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits((long) 1)
                        .writeCapacityUnits((long) 1).build())
                .attributeDefinitions(attributeDefinitions).keySchema(tableKeySchema).build();

        try {
            dynamoDbClient.createTable(createTableRequest);
        }
        catch (ResourceInUseException e) {
            // table already exists, do nothing
        }
    }

    public static void cleanUp(DynamoDbTable<MovieDetails> dynamoDbTable, String uuid) {
        dynamoDbTable.deleteItem(Key.builder().partitionValue(uuid).build());
    }

}
