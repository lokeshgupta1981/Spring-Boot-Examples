package com.howtodoinjava.demo;

import com.howtodoinjava.demo.model.Recipe;
import com.howtodoinjava.demo.repository.RecipeRepository;
import com.howtodoinjava.demo.service.RecipeCacheService;
import java.time.Duration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Runs a few Redis operations on startup when the app is started with "mvn spring-boot:run".
 * Not active during tests (profile "test" is excluded).
 */
@Configuration
@Profile("!test")
public class RedisDemoRunner {

  @Bean
  CommandLineRunner demo(RecipeCacheService service, RecipeRepository repository,
      RedisConnectionFactory connectionFactory) {
    return args -> {
      System.out.println("Connection factory : " + connectionFactory.getClass().getSimpleName());

      service.saveName("recipe:1:name", "Pancakes", Duration.ofMinutes(10));
      System.out.println("GET recipe:1:name  : " + service.findName("recipe:1:name"));
      System.out.println("TTL recipe:1:name  : " + service.remainingSeconds("recipe:1:name"));
      System.out.println("INCR recipe:1:views: " + service.countView("recipe:1:views"));
      System.out.println("INCR recipe:1:views: " + service.countView("recipe:1:views"));

      service.saveRecipe("recipe:2", new Recipe("2", "Omelette", 10));
      System.out.println("GET recipe:2       : " + service.findRecipe("recipe:2"));

      Recipe saved = repository.save(new Recipe("3", "Lemonade", 5));
      System.out.println("repository.save    : " + saved);
      System.out.println("repository.findById: " + repository.findById("3").orElse(null));
      System.out.println("repository.count   : " + repository.count());
    };
  }
}
