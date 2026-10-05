package com.howtodoinjava.springboothibernate;

import java.math.BigDecimal;

public record RepairJobView(Long id, String bikeModel, String problem, RepairStatus status,
                            BigDecimal cost, String mechanic) {

  static RepairJobView of(RepairJob job) {
    return new RepairJobView(job.getId(), job.getBikeModel(), job.getProblem(), job.getStatus(),
        job.getCost(), job.getMechanic().getName());
  }
}
