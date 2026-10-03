package com.howtodoinjava.springboothibernate;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManagerFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HibernateApiTest {

  @Autowired
  RepairJobDao dao;

  @Autowired
  EntityManagerFactory emf;

  @Autowired
  SessionFactory sessionFactory;                     // the same bean, injected by its Hibernate type

  @Test
  void entityManagerQueries() {
    assertThat(dao.findOpenJobs()).extracting(RepairJob::getBikeModel).contains("Giant", "Brompton");
    assertThat(dao.findByBikeModel("Brompton").getProblem()).isEqualTo("gears");
    assertThat(dao.findByBikeModel("Nope")).isNull();
  }

  @Test
  void unwrapSession() {
    assertThat(dao.findMultiple(List.of(1L, 4L))).extracting(RepairJob::getBikeModel)
        .containsExactly("Trek", "Cube");
  }

  @Test
  void unwrapSessionFactory() {
    assertThat(dao.dialect()).isEqualTo("H2Dialect 2.4.240");
    assertThat(emf.unwrap(SessionFactory.class)).isNotNull();
    assertThat(sessionFactory).isInstanceOf(EntityManagerFactory.class);
  }

  @Test
  void hibernate7RemovedTheLegacySessionMethods() {
    List<String> names = Arrays.stream(Session.class.getMethods()).map(Method::getName).toList();
    assertThat(names).doesNotContain("save", "saveOrUpdate", "update", "delete");
    assertThat(names).contains("persist", "merge", "remove", "find", "getReference", "findMultiple");
  }
}
