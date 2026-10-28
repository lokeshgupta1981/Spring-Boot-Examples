package com.howtodoinjava.retry;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RateService {
    private final RateGateway gateway;

    public RateService(RateGateway gateway) {
        this.gateway = gateway;
    }

    @Retryable(includes = TransientRateException.class, maxRetries = 2, delay = 0)
    public int quote(String destination) {
        return gateway.fetchRate(destination);
    }
}
