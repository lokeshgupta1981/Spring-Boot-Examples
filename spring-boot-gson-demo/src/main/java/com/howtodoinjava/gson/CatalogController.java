package com.howtodoinjava.gson;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogController {
    private final Gson gson;

    public CatalogController(Gson gson) {
        this.gson = gson;
    }

    @GetMapping("/catalog/item")
    public CatalogItem item() {
        return new CatalogItem();
    }

    @GetMapping(value = "/catalog/manual", produces = MediaType.APPLICATION_JSON_VALUE)
    public String manualJson() {
        return gson.toJson(new CatalogItem());
    }
}
