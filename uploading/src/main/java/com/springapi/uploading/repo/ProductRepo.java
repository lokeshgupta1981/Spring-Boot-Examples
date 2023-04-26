package com.springapi.uploading.repo;

import com.springapi.uploading.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo  extends JpaRepository<Product, Long> {

}