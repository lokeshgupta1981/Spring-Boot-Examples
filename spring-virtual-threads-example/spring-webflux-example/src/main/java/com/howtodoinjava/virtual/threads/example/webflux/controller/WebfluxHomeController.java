package com.howtodoinjava.virtual.threads.example.webflux.controller;

import com.howtodoinjava.virtual.threads.example.webflux.model.Product;
import com.howtodoinjava.virtual.threads.example.webflux.repository.ProductRepo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webflux")
public class WebfluxHomeController {

    @Autowired
    ProductRepo productRepository;

    @GetMapping("/thread")
    public Flux<Product> webfluxThreadProducts() throws InterruptedException {
        Thread.sleep(1000);
        return productRepository.findAll();
    }

    @PostMapping("/save")
    public String webfluxSaveProduct() throws InterruptedException {
        for(int i = 0; i < 1000; i++){
            Product product = new Product();
            product.setProductName(RandomStringUtils.randomAlphanumeric(5));
            product.setPrice(RandomUtils.nextLong(10,1000));
            product.setPrice(1L);
            Mono<Product> savedData = productRepository.save(product);
            System.out.println(savedData.toString());
        }
        return "Success";
    }
}