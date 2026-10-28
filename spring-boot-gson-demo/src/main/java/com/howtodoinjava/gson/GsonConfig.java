package com.howtodoinjava.gson;

import java.time.LocalDate;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.springframework.boot.gson.autoconfigure.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {
    @Bean
    GsonBuilderCustomizer localDateAsIsoString() {
        return builder -> builder.registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (date, type, context) ->
                        new JsonPrimitive(date.toString()));
    }
}
