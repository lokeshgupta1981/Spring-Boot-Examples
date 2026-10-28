package com.howtodoinjava.referenceid.autoconfigure;

import com.howtodoinjava.referenceid.ReferenceIdFormatter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(ReferenceIdFormatter.class)
@EnableConfigurationProperties(ReferenceIdProperties.class)
public class ReferenceIdAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    ReferenceIdFormatter referenceIdFormatter(ReferenceIdProperties properties) {
        return new ReferenceIdFormatter(properties.getPrefix());
    }
}
