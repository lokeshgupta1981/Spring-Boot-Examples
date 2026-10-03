package com.howtodoinjava.springboothibernate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@SpringBootTest
@Testcontainers
class MySqlRepairJobTest {

  @Container
  @ServiceConnection
  static MySQLContainer mysql = new MySQLContainer("mysql:9");

  @Autowired
  RepairShopService service;

  @Autowired
  MechanicRepository mechanics;

  @Autowired
  RepairJobRepository jobs;

  @Autowired
  RepairJobDao dao;

  @Test
  void runsTheSameCodeOnMySql() {
    assertThat(dao.dialect()).startsWith("MySQLDialect 9.");
    assertThat(jobs.count()).isZero();                  // data.sql runs only for embedded databases

    Mechanic lena = mechanics.save(new Mechanic("Lena"));
    RepairJob job = service.receive(lena.getId(), "Trek", "flat tire", new BigDecimal("15.00"));
    service.complete(job.getId());

    assertThat(jobs.findByStatus(RepairStatus.DONE)).extracting(RepairJob::getBikeModel).containsExactly("Trek");
    assertThat(jobs.revenuePerMechanic()).containsExactly(new MechanicRevenue("Lena", new BigDecimal("15.00")));
    assertThat(jobs.ticketLabels()).containsExactly("Trek: flat tire");
  }

  @Test
  void checkConstraintWorksOnMySql() {
    Mechanic sam = mechanics.save(new Mechanic("Sam"));
    assertThatThrownBy(() -> service.receive(sam.getId(), "Bmx", "chain", new BigDecimal("-5.00")))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
