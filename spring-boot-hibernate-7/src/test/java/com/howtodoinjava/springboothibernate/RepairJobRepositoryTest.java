package com.howtodoinjava.springboothibernate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class RepairJobRepositoryTest {

  @Autowired
  RepairJobRepository jobs;

  @Autowired
  MechanicRepository mechanics;

  @Autowired
  EntityManager em;

  @Test
  void derivedQueries() {
    assertThat(jobs.findByStatus(RepairStatus.DONE)).extracting(RepairJob::getBikeModel)
        .containsExactly("Trek", "Cube");
    assertThat(jobs.findByMechanicNameAndStatusOrderByCostDesc("Anna", RepairStatus.IN_PROGRESS))
        .extracting(RepairJob::getBikeModel).containsExactly("Giant");
    assertThat(jobs.countByStatus(RepairStatus.DONE)).isEqualTo(2);
    assertThat(jobs.existsByBikeModelAndStatusNot("Giant", RepairStatus.DONE)).isTrue();
    assertThat(jobs.existsByBikeModelAndStatusNot("Trek", RepairStatus.DONE)).isFalse();
    assertThat(mechanics.findByName("Ravi")).isPresent();
  }

  @Test
  void queryMethods() {
    assertThat(jobs.findCostingAtLeast(new BigDecimal("25"))).extracting(RepairJob::getBikeModel)
        .containsExactly("Giant", "Cube", "Brompton");
    assertThat(jobs.revenuePerMechanic()).containsExactly(
        new MechanicRevenue("Anna", new BigDecimal("15.00")),
        new MechanicRevenue("Ravi", new BigDecimal("30.00")));
    assertThat(jobs.ticketLabels())
        .containsExactly("Trek: flat tire", "Giant: brakes", "Brompton: gears", "Cube: chain");
  }

  @Test
  void joinFetchLoadsTheMechanic() {
    RepairJob job = jobs.findWithMechanic(3L).orElseThrow();
    em.clear();                                         // detach: a lazy proxy would fail now
    assertThat(job.getMechanic().getName()).isEqualTo("Ravi");
  }

  @Test
  void modifyingQuery() {
    assertThat(jobs.moveAll(RepairStatus.RECEIVED, RepairStatus.IN_PROGRESS)).isEqualTo(1);
    em.clear();
    assertThat(jobs.countByStatus(RepairStatus.IN_PROGRESS)).isEqualTo(2);
  }

  @Test
  void namingStrategyCreatesSnakeCaseColumns() {
    Object column = em.createNativeQuery("""
            select column_name from information_schema.columns
            where table_name = 'REPAIR_JOB' and column_name = 'BIKE_MODEL'""")
        .getSingleResult();
    assertThat(column).isEqualTo("BIKE_MODEL");
  }

  @Test
  void checkConstraintRejectsNegativeCost() {
    Mechanic anna = mechanics.findByName("Anna").orElseThrow();
    RepairJob job = new RepairJob("Bmx", "chain", new BigDecimal("-5"));
    anna.addJob(job);
    assertThatThrownBy(() -> jobs.saveAndFlush(job))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("CONSTRAINT_");
  }

  @Test
  void creationTimestampIsSet() {
    Mechanic anna = mechanics.findByName("Anna").orElseThrow();
    RepairJob job = new RepairJob("Bmx", "chain", new BigDecimal("10.00"));
    anna.addJob(job);
    jobs.saveAndFlush(job);
    assertThat(job.getId()).isNotNull();
    assertThat(job.getCreatedAt()).isNotNull();
    assertThat(job.getStatus()).isEqualTo(RepairStatus.RECEIVED);
  }
}
