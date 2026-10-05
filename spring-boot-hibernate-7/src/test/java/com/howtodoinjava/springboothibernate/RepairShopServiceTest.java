package com.howtodoinjava.springboothibernate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RepairShopServiceTest {

  @Autowired
  RepairShopService service;

  @Autowired
  RepairJobRepository jobs;

  @Test
  void receiveSavesANewJob() {
    RepairJob job = service.receive(1L, "Canyon", "flat tire", new BigDecimal("15.00"));
    assertThat(job.getId()).isNotNull();
    assertThat(job.getCreatedAt()).isNotNull();
    assertThat(service.find(job.getId()).getMechanic().getName()).isEqualTo("Anna");
  }

  @Test
  void completeUsesDirtyCheckingWithoutSave() {
    RepairJob job = service.receive(2L, "Orbea", "gears", new BigDecimal("20.00"));
    service.complete(job.getId());
    assertThat(service.find(job.getId()).getStatus()).isEqualTo(RepairStatus.DONE);
  }

  @Test
  void runtimeExceptionRollsBackTheWholeMethod() {
    long before = jobs.count();
    List<RepairJob> batch = List.of(
        new RepairJob("Scott", "brakes", new BigDecimal("30.00")),
        new RepairJob("Bianchi", "chain", new BigDecimal("-1.00")));
    assertThatThrownBy(() -> service.receiveAll(1L, batch))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Negative cost for Bianchi");
    assertThat(jobs.count()).isEqualTo(before);           // Scott was rolled back too
  }

  @Test
  void readOnlyTransactionDoesNotWriteChanges() {
    RepairJob job = service.receive(1L, "Merida", "brakes", new BigDecimal("40.00"));
    service.changeCostInReadOnly(job.getId(), new BigDecimal("99.00"));
    assertThat(service.find(job.getId()).getCost()).isEqualByComparingTo("40.00");
  }

  @Test
  void lazyAssociationOutsideTransactionFails() {
    RepairJob job = jobs.findById(1L).orElseThrow();      // transaction ends when findById() returns
    assertThatThrownBy(() -> job.getMechanic().getName())
        .isInstanceOf(LazyInitializationException.class)
        .hasMessageContaining("no session");
  }

  @Test
  void unknownMechanicThrows() {
    assertThatThrownBy(() -> service.receive(99L, "Trek", "brakes", BigDecimal.TEN))
        .isInstanceOf(JobNotFoundException.class)
        .hasMessage("Mechanic 99 not found");
  }
}
