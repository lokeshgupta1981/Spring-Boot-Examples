package com.howtodoinjava.virtual.threads.example.repository;

import com.howtodoinjava.virtual.threads.example.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}