package com.howtodoinjava.virtual.threads.example.webflux.repository;

import com.howtodoinjava.virtual.threads.example.webflux.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends ReactiveCrudRepository<Product,Long> {
}