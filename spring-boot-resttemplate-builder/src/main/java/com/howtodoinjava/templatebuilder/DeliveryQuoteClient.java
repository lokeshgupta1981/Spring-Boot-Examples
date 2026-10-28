package com.howtodoinjava.templatebuilder;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DeliveryQuoteClient {
    private final RestTemplate restTemplate;

    public DeliveryQuoteClient(RestTemplateBuilder builder,
                               @Value("${delivery.base-url}") String baseUrl) {
        this.restTemplate = builder
                .baseUri(baseUrl)
                .defaultHeader("X-Application", "checkout")
                .connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(3))
                .build();
    }

    public DeliveryQuote quote(String postalCode) {
        return restTemplate.getForObject("/quotes/{postalCode}", DeliveryQuote.class, postalCode);
    }

    RestTemplate restTemplate() {
        return restTemplate;
    }

    public record DeliveryQuote(String postalCode, int days) {}
}
