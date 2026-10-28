package com.howtodoinjava.remoting;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface InventoryClient {
    @GetExchange("/inventory/{sku}")
    InventoryController.Stock stock(@PathVariable String sku);
}
