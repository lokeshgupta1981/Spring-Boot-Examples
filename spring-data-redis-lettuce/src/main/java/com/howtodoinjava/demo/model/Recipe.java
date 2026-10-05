package com.howtodoinjava.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 * Stored by RecipeRepository as a Redis hash under the key "recipe:{id}".
 * The TimeToLive field sets the expiry of that hash in seconds (null means no expiry).
 */
@RedisHash("recipe")
public class Recipe {

  @Id
  private String id;
  private String name;
  private int minutes;

  @TimeToLive
  private Long ttlSeconds;

  public Recipe() {
  }

  public Recipe(String id, String name, int minutes) {
    this.id = id;
    this.name = name;
    this.minutes = minutes;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getMinutes() {
    return minutes;
  }

  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  public Long getTtlSeconds() {
    return ttlSeconds;
  }

  public void setTtlSeconds(Long ttlSeconds) {
    this.ttlSeconds = ttlSeconds;
  }

  @Override
  public String toString() {
    return "Recipe[id=" + id + ", name=" + name + ", minutes=" + minutes + "]";
  }
}
