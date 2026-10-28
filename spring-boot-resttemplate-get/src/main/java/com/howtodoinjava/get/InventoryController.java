package com.howtodoinjava.get;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {
    public record Stock(String sku, String warehouse, int available) {}

    @GetMapping("/inventory/{sku}")
    ResponseEntity<Stock> stock(@PathVariable String sku,
                                @RequestParam(defaultValue = "main") String warehouse,
                                @RequestHeader(value = "X-Request-Id", required = false) String requestId) {
        if (!sku.equals("BOOK-42")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("X-Request-Id", requestId == null ? "none" : requestId)
                .body(new Stock(sku, warehouse, warehouse.equals("west") ? 4 : 12));
    }
}
