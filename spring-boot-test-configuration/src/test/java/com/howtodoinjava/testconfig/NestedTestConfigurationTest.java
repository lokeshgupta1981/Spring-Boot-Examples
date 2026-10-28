package com.howtodoinjava.testconfig;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NestedTestConfigurationTest {
    @Autowired InvoiceDueDateService service;

    @TestConfiguration(proxyBeanMethods = false)
    static class FixedClockConfiguration {
        @Bean @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-10-28T00:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Test
    void calculatesDueDateFromFixedClock() {
        assertThat(service.dueDate()).isEqualTo(LocalDate.of(2026, 11, 11));
    }
}
