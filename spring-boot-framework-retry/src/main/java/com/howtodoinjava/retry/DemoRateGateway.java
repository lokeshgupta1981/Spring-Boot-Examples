package com.howtodoinjava.retry;

import org.springframework.stereotype.Component;

@Component
public class DemoRateGateway implements RateGateway {
    @Override
    public int fetchRate(String destination) {
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination is required");
        }
        return 12;
    }
}
