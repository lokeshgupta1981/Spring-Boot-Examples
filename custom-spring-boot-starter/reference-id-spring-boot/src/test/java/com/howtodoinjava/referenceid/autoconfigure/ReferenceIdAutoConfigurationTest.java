package com.howtodoinjava.referenceid.autoconfigure;

import com.howtodoinjava.referenceid.ReferenceIdFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class ReferenceIdAutoConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ReferenceIdAutoConfiguration.class));

    @Test void usesDefaultPrefix() {
        runner.run(context -> assertThat(context.getBean(ReferenceIdFormatter.class).format(42))
                .isEqualTo("ORD-000042"));
    }

    @Test void bindsApplicationProperty() {
        runner.withPropertyValues("reference-id.prefix=PAY")
                .run(context -> assertThat(context.getBean(ReferenceIdFormatter.class).format(42))
                        .isEqualTo("PAY-000042"));
    }

    @Test void backsOffForApplicationBean() {
        runner.withUserConfiguration(UserFormatterConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ReferenceIdFormatter.class);
                    assertThat(context.getBean(ReferenceIdFormatter.class).format(42))
                            .isEqualTo("CUSTOM-000042");
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class UserFormatterConfiguration {
        @Bean
        ReferenceIdFormatter customFormatter() {
            return new ReferenceIdFormatter("CUSTOM");
        }
    }
}
