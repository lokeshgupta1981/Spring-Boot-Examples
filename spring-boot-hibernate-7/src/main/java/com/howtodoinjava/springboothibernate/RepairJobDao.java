package com.howtodoinjava.springboothibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class RepairJobDao {

  private final EntityManager em;
  private final EntityManagerFactory emf;

  public RepairJobDao(EntityManager em, EntityManagerFactory emf) {
    this.em = em;
    this.emf = emf;
  }

  // Plain Jakarta Persistence: JPQL through the EntityManager
  public List<RepairJob> findOpenJobs() {
    return em.createQuery("select j from RepairJob j where j.status <> :done order by j.id", RepairJob.class)
        .setParameter("done", RepairStatus.DONE)
        .getResultList();
  }

  // Jakarta Persistence 3.2: getSingleResultOrNull()
  public RepairJob findByBikeModel(String bikeModel) {
    return em.createQuery("select j from RepairJob j where j.bikeModel = :model", RepairJob.class)
        .setParameter("model", bikeModel)
        .getSingleResultOrNull();
  }

  // Hibernate API: unwrap the Session behind the EntityManager
  public List<RepairJob> findMultiple(List<Long> ids) {
    Session session = em.unwrap(Session.class);
    return session.findMultiple(RepairJob.class, ids);
  }

  // Hibernate API: unwrap the SessionFactory behind the EntityManagerFactory
  public String dialect() {
    SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
    Dialect dialect = sessionFactory.unwrap(SessionFactoryImplementor.class).getJdbcServices().getDialect();
    return dialect.getClass().getSimpleName() + " " + dialect.getVersion();
  }
}
