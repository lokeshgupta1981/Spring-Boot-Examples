package com.howtodoinjava.demo.junit5;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith(SpringExtension.class)
@Import(AppConfig.class)
public class HelloTest {

  @Autowired
  ApplicationContext context;

  @Test
  void contextLoads() {
    Assertions.assertNotNull(context);

    TestBean testBean = (TestBean) context.getBean("testBean");
    Assertions.assertEquals("Test Message", testBean.name());
  }
}

@TestConfiguration
class AppConfig {

  @Bean
  TestBean testBean() {
    return new TestBean("Test Message");
  }

}

record TestBean(String name) {
}

