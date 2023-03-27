package com.howtodoinjava.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document("groceryitems")
public class GroceryItem {

        @Id
        @Field("id")
        private Integer id;
        @Field("name")
        private String name;
        @Field("quantity")
        private int quantity;
        @Field("category")
        private String category;
}