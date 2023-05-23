package com.howtodoinjava.demo;

import com.howtodoinjava.demo.entity.MovieDetails;
import com.howtodoinjava.demo.service.DynamoDbOperationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class AppTests {

  @Autowired
  private DynamoDbOperationService dbOperationService;

  @Autowired
  private DynamoDbEnhancedClient dynamoDbEnhancedClient;

  DynamoDbTable<MovieDetails> dynamoDbTable;

  @BeforeAll
  void setup() {

    dynamoDbTable = dynamoDbEnhancedClient
        .table("movie_details", TableSchema.fromBean(MovieDetails.class));

    dynamoDbTable.createTable();
  }

  @AfterAll
  void cleanup() {
    dynamoDbTable.deleteTable();
  }

  @Test
  void testCrud() {
    MovieDetails movieDetails = new MovieDetails("MOV001", "Avengers", null, "Action", "US", "175",
        "English");
    MovieDetails savedMovie = dbOperationService.saveData(movieDetails);

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

  private static void describeAndCreateTable(DynamoDbClient dynamoDbClient,
      @Nullable String tablePrefix) {
    ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
    attributeDefinitions.add(
        AttributeDefinition.builder().attributeName("id").attributeType("S").build());

    ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
    tableKeySchema.add(
        KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build());

    String tableName =
        StringUtils.hasText(tablePrefix) ? tablePrefix.concat("movie_details") : "movie_details";
    CreateTableRequest createTableRequest = CreateTableRequest.builder().tableName(tableName)
        .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits((long) 1)
            .writeCapacityUnits((long) 1).build())
        .attributeDefinitions(attributeDefinitions).keySchema(tableKeySchema).build();

    try {
      dynamoDbClient.createTable(createTableRequest);
    } catch (ResourceInUseException e) {
      // table already exists, do nothing
    }
  }

  public static void cleanUp(DynamoDbTable<MovieDetails> dynamoDbTable, String uuid) {
    dynamoDbTable.deleteItem(Key.builder().partitionValue(uuid).build());
  }

}
