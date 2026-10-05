package com.howtodoinjava.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.howtodoinjava.demo.model.Recipe;
import com.howtodoinjava.demo.repository.RecipeRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class RecipeRepositoryTest {

  @Autowired
  RecipeRepository repository;

  @Autowired
  StringRedisTemplate stringRedisTemplate;

  @Test
  void savesRecipeAsHash() {
    Recipe recipe = new Recipe("3", "Lemonade", 5);
    recipe.setTtlSeconds(300L);
    repository.save(recipe);

    Recipe found = repository.findById("3").orElseThrow();
    assertThat(found.getName()).isEqualTo("Lemonade");
    assertThat(repository.count()).isEqualTo(1L);

    // The repository writes one hash per object under "recipe:{id}"
    Map<Object, Object> hash = stringRedisTemplate.opsForHash().entries("recipe:3");
    assertThat(hash).containsEntry("name", "Lemonade").containsEntry("minutes", "5");
    assertThat(stringRedisTemplate.getExpire("recipe:3")).isBetween(290L, 300L);

    repository.deleteById("3");
    assertThat(repository.existsById("3")).isFalse();
  }
}
