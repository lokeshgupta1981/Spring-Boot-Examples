package com.howtodoinjava.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI recipeOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Recipe API")
            .version("1.0.0")
            .description("Recipes and their preparation time")
            .contact(new Contact().name("howtodoinjava").url("https://howtodoinjava.com"))
            .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
        .components(new Components().addSecuritySchemes("bearerAuth",
            new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }

  @Bean
  public GroupedOpenApi recipesGroup() {
    return GroupedOpenApi.builder()
        .group("recipes")
        .pathsToMatch("/recipes/**")
        .build();
  }

  @Bean
  public GroupedOpenApi adminGroup() {
    return GroupedOpenApi.builder()
        .group("admin")
        .pathsToMatch("/admin/**")
        .build();
  }
}
