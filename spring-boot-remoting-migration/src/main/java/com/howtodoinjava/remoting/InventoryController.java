package com.howtodoinjava.remoting;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    public record Stock(String sku, int available) {}

    @GetMapping("/{sku}")
    public ResponseEntity<Stock> stock(@PathVariable String sku) {
        if (!"BOOK-1".equals(sku)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Stock(sku, 7));
    }
}
