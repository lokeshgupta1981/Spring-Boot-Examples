package com.howtodoinjava.testconfig;

import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class InvoiceDueDateService {
    private final Clock clock;

    public InvoiceDueDateService(Clock clock) {
        this.clock = clock;
    }

    public LocalDate dueDate() {
        return LocalDate.now(clock).plusDays(14);
    }
}
