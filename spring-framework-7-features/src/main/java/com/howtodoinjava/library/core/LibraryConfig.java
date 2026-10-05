package com.howtodoinjava.library.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.resilience.annotation.EnableResilientMethods;

@Configuration
@EnableResilientMethods                       // enables @Retryable and @ConcurrencyLimit
@Import(LibraryRegistrar.class)               // a BeanRegistrar registers the beans
public class LibraryConfig {
}
