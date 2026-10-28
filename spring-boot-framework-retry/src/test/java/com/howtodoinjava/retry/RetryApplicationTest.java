package com.howtodoinjava.retry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class RetryApplicationTest {
    @Autowired RateService service;
    @Autowired QuoteFacade facade;
    @MockitoBean RateGateway gateway;

    @BeforeEach void resetGateway() {
        reset(gateway);
    }

    @Test void transientFailureThenSuccess() {
        when(gateway.fetchRate("Delhi"))
                .thenThrow(new TransientRateException("temporary outage"))
                .thenReturn(12);

        assertEquals(12, service.quote("Delhi"));
        verify(gateway, times(2)).fetchRate("Delhi");
    }

    @Test void exhaustedFailurePropagatesLastOriginalException() {
        when(gateway.fetchRate("Delhi"))
                .thenThrow(new TransientRateException("still unavailable"));

        assertEquals("still unavailable", assertThrows(TransientRateException.class,
                () -> service.quote("Delhi")).getMessage());
        verify(gateway, times(3)).fetchRate("Delhi");
    }

    @Test void nonIncludedFailureIsNotRetried() {
        when(gateway.fetchRate(""))
                .thenThrow(new IllegalArgumentException("Destination is required"));

        assertThrows(IllegalArgumentException.class, () -> service.quote(""));
        verify(gateway, times(1)).fetchRate("");
    }

    @Test void callerCanReturnExplicitFallbackAfterExhaustion() {
        when(gateway.fetchRate("Delhi"))
                .thenThrow(new TransientRateException("still unavailable"));

        assertEquals(new QuoteFacade.QuoteResult(false, null), facade.lookup("Delhi"));
        verify(gateway, times(3)).fetchRate("Delhi");
    }
}
