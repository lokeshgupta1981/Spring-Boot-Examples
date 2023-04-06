package com.howtodoinjava.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("items")
public class Item {

  @Id
  private String id;

  @Field("name")
  private String name;

  @Field("quantity")
  private int quantity;
  
  @Field("category")
  private String category;
}