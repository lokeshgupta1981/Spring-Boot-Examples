package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.model.Recipe;
import java.time.Duration;
import java.util.Map;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RecipeCacheService {

  private final StringRedisTemplate stringRedisTemplate;
  private final RedisTemplate<String, Recipe> recipeRedisTemplate;

  // One operations group injected by the template bean name, so we skip opsForValue() each time
  @Resource(name = "stringRedisTemplate")
  private ValueOperations<String, String> valueOps;

  public RecipeCacheService(StringRedisTemplate stringRedisTemplate,
      RedisTemplate<String, Recipe> recipeRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.recipeRedisTemplate = recipeRedisTemplate;
  }

  // ---- String values with StringRedisTemplate ----

  public void saveName(String key, String name, Duration ttl) {
    stringRedisTemplate.opsForValue().set(key, name, ttl);
  }

  public String findName(String key) {
    return stringRedisTemplate.opsForValue().get(key);
  }

  public Long countView(String key) {
    return valueOps.increment(key);
  }

  public Long remainingSeconds(String key) {
    return stringRedisTemplate.getExpire(key);
  }

  public Boolean delete(String key) {
    return stringRedisTemplate.delete(key);
  }

  // ---- Objects as JSON with RedisTemplate<String, Recipe> ----

  public void saveRecipe(String key, Recipe recipe) {
    recipeRedisTemplate.opsForValue().set(key, recipe);
  }

  public Recipe findRecipe(String key) {
    return recipeRedisTemplate.opsForValue().get(key);
  }

  public void addToMenu(String menuKey, Recipe recipe) {
    recipeRedisTemplate.opsForHash().put(menuKey, recipe.getId(), recipe);
  }

  public Map<Object, Object> menu(String menuKey) {
    return recipeRedisTemplate.opsForHash().entries(menuKey);
  }

  public Boolean expireMenu(String menuKey, Duration ttl) {
    return recipeRedisTemplate.expire(menuKey, ttl);
  }
}
