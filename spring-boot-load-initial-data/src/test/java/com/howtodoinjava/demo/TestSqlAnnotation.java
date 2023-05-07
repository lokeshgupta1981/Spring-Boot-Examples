package com.howtodoinjava.demo;

import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

import com.howtodoinjava.demo.entity.Item;
import com.howtodoinjava.demo.repository.ItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.jdbc.SqlMergeMode;

@SpringBootTest
/*@Sql("/sql/schema.sql")
@SqlConfig(errorMode = ErrorMode.CONTINUE_ON_ERROR)*/
@SqlMergeMode(MERGE)
@Sql({"/sql/items-grocery.sql"})
public class TestSqlAnnotation {

  @Autowired
  ItemRepository itemRepository;

  @Test
  //1 - Multiple files in same annotation
  //@Sql({"/sql/items-grocery.sql", "/sql/items-books.sql"})
  //2 - Repeatable annotation

  @Sql({"/sql/items-books.sql"})
  //3 - SqlGroup
  /*@SqlGroup({
      @Sql({"/sql/items-grocery.sql"}),
      @Sql({"/sql/items-books.sql"})
  })*/

  //Statements
  /*@Sql(statements = {
      "INSERT INTO ITEM (name) VALUES ('Book-1');",
      "INSERT INTO ITEM (name) VALUES ('Grocery-1');"
  }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)*/
  //1
  /*@Sql(
      scripts = {"/sql/items-grocery.sql", "/sql/items-books.sql"},
      config = @SqlConfig(errorMode = ErrorMode.CONTINUE_ON_ERROR)
  )*/
 /* @SqlMergeMode(MERGE)
  @Sql(scripts = {"/sql/items-books.sql"},
      executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)*/
  void testLoadScripts() {
    Optional<Item> gItem = itemRepository.findByName("Grocery-1");
    Optional<Item> bItem = itemRepository.findByName("Book-1");

    Assertions.assertNotNull(gItem.get());
    Assertions.assertNotNull(bItem.get());
  }
}
