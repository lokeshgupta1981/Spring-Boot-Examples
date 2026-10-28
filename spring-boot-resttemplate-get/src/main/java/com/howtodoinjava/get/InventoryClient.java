package com.howtodoinjava.get;

import java.net.URI;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class InventoryClient {
    private final RestTemplate restTemplate;

    public InventoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public InventoryController.Stock body(String baseUrl, String sku, String warehouse) {
        URI uri = uri(baseUrl, sku, warehouse);
        return restTemplate.getForObject(uri, InventoryController.Stock.class);
    }

    public ResponseEntity<InventoryController.Stock> response(String baseUrl, String sku, String warehouse) {
        URI uri = uri(baseUrl, sku, warehouse);
        return restTemplate.getForEntity(uri, InventoryController.Stock.class);
    }

    public ResponseEntity<InventoryController.Stock> withRequestId(String baseUrl, String sku,
                                                                    String warehouse, String requestId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        return restTemplate.exchange(uri(baseUrl, sku, warehouse), HttpMethod.GET,
                new HttpEntity<>(headers), InventoryController.Stock.class);
    }

    private URI uri(String baseUrl, String sku, String warehouse) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("inventory", sku)
                .queryParam("warehouse", warehouse)
                .build()
                .encode()
                .toUri();
    }
}
