package com.howtodoinjava.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.howtodoinjava.demo.model.Recipe;
import com.howtodoinjava.demo.service.RecipeCacheService;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class LettuceRedisTemplateTest {

  @Autowired
  RedisConnectionFactory connectionFactory;

  @Autowired
  StringRedisTemplate stringRedisTemplate;

  @Autowired
  RecipeCacheService service;

  @Test
  void usesLettuceWithConnectionPool() {
    assertThat(connectionFactory).isInstanceOf(LettuceConnectionFactory.class);

    LettuceConnectionFactory lettuce = (LettuceConnectionFactory) connectionFactory;
    assertThat(lettuce.getClientConfiguration()).isInstanceOf(LettucePoolingClientConfiguration.class);
    assertThat(lettuce.getClientName()).isEqualTo("recipe-app");
  }

  @Test
  void storesAndExpiresStringValues() {
    service.saveName("recipe:1:name", "Pancakes", Duration.ofMinutes(10));

    assertThat(service.findName("recipe:1:name")).isEqualTo("Pancakes");
    assertThat(service.remainingSeconds("recipe:1:name")).isBetween(590L, 600L);
    assertThat(service.countView("recipe:1:views")).isEqualTo(1L);
    assertThat(service.countView("recipe:1:views")).isEqualTo(2L);
    assertThat(service.delete("recipe:1:name")).isTrue();
    assertThat(service.findName("recipe:1:name")).isNull();
  }

  @Test
  void storesObjectsAsJson() {
    service.saveRecipe("recipe:2", new Recipe("2", "Omelette", 10));

    Recipe found = service.findRecipe("recipe:2");
    assertThat(found.getName()).isEqualTo("Omelette");
    assertThat(found.getMinutes()).isEqualTo(10);

    // The value is readable JSON because the template uses JacksonJsonRedisSerializer
    String raw = stringRedisTemplate.opsForValue().get("recipe:2");
    assertThat(raw).isEqualTo("{\"id\":\"2\",\"name\":\"Omelette\",\"minutes\":10,\"ttlSeconds\":null}");
  }

  @Test
  void storesObjectsInAHash() {
    service.addToMenu("menu:monday", new Recipe("2", "Omelette", 10));
    service.addToMenu("menu:monday", new Recipe("3", "Lemonade", 5));

    Map<Object, Object> menu = service.menu("menu:monday");
    assertThat(menu).hasSize(2);
    assertThat(((Recipe) menu.get("3")).getName()).isEqualTo("Lemonade");
    assertThat(service.expireMenu("menu:monday", Duration.ofHours(1))).isTrue();
  }
}
