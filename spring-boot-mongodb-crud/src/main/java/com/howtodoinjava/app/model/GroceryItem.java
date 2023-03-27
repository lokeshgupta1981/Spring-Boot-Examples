package com.howtodoinjava.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("groceryitems")
public class GroceryItem {

        @Id
        private Integer id;
        private String name;
        private int quantity;
        private String category;
}