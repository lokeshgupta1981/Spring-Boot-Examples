package com.howtodoinjava.cors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {
    public record Stock(String sku, int available) {}
    public record Report(String day, int orders) {}

    @CrossOrigin(origins = "https://shop.example", methods = RequestMethod.GET)
    @GetMapping("/api/stock/{sku}")
    public Stock stock(@PathVariable String sku) {
        return new Stock(sku, 7);
    }

    @GetMapping("/api/reports/{day}")
    public Report report(@PathVariable String day) {
        return new Report(day, 23);
    }
}
