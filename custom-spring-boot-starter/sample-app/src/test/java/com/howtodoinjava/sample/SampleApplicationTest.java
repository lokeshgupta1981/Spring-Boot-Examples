package com.howtodoinjava.sample;

import com.howtodoinjava.referenceid.ReferenceIdFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SampleApplicationTest {
    @Autowired ReferenceIdFormatter formatter;

    @Test void importsStarterAutoConfiguration() {
        assertThat(formatter.format(42)).isEqualTo("ORD-000042");
    }
}
