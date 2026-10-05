package com.howtodoinjava.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.howtodoinjava.demo.service.RecipeCacheService;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * The same app and the same code, with the Jedis client instead of Lettuce.
 */
@SpringBootTest(properties = "spring.data.redis.client-type=jedis")
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class JedisClientTest {

  @Autowired
  RedisConnectionFactory connectionFactory;

  @Autowired
  RecipeCacheService service;

  @Test
  void usesJedisWithConnectionPool() {
    assertThat(connectionFactory).isInstanceOf(JedisConnectionFactory.class);

    JedisConnectionFactory jedis = (JedisConnectionFactory) connectionFactory;
    assertThat(jedis.getUsePool()).isTrue();
    assertThat(jedis.getPoolConfig().getMaxTotal()).isEqualTo(16);
  }

  @Test
  void readsAndWritesWithJedis() {
    service.saveName("recipe:9:name", "Waffles", Duration.ofMinutes(1));
    assertThat(service.findName("recipe:9:name")).isEqualTo("Waffles");
  }
}
