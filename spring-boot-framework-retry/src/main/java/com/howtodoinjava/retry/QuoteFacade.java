package com.howtodoinjava.retry;

import org.springframework.stereotype.Component;

@Component
public class QuoteFacade {
    private final RateService rateService;

    public QuoteFacade(RateService rateService) {
        this.rateService = rateService;
    }

    public QuoteResult lookup(String destination) {
        try {
            return new QuoteResult(true, rateService.quote(destination));
        } catch (TransientRateException ex) {
            return new QuoteResult(false, null);
        }
    }

    public record QuoteResult(boolean available, Integer amount) {}
}
