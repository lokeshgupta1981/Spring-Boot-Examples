package com.howtodoinjava.testconfig;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(FixedDateTestConfiguration.class)
class ImportedTestConfigurationTest {
    @Autowired InvoiceDueDateService service;

    @Test
    void calculatesDueDateFromImportedClock() {
        assertThat(service.dueDate()).isEqualTo(LocalDate.of(2027, 1, 15));
    }
}
