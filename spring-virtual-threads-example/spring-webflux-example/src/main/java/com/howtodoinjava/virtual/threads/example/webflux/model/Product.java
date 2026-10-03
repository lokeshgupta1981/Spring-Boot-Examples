package com.howtodoinjava.virtual.threads.example.webflux.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("product")
public class Product {

    @Id
    private Long id;
    private String productName;
    private Long price;

}