package com.howtodoinjava.virtual.threads.example.controller;

import com.howtodoinjava.virtual.threads.example.model.Product;
import com.howtodoinjava.virtual.threads.example.repository.ProductRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/thread")
    public List<Product> checkThread() throws InterruptedException {
        Thread.sleep(1000);
        return productRepository.findAll();
    }

    @PostMapping("/save")
    public String saveProduct() throws InterruptedException {
        for(int i = 0; i < 1000; i++){
            Product product = new Product();
            product.setProductName(RandomStringUtils.randomAlphanumeric(5));
            product.setPrice(RandomUtils.nextLong(10,1000));
            product.setPrice(1L);
            productRepository.save(product);
        }
        return "Success";
    }
}