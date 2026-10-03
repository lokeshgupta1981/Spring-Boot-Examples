package com.howtodoinjava.springboothibernate;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RepairShopService {

  private final RepairJobRepository jobs;
  private final MechanicRepository mechanics;

  public RepairShopService(RepairJobRepository jobs, MechanicRepository mechanics) {
    this.jobs = jobs;
    this.mechanics = mechanics;
  }

  @Transactional
  public RepairJob receive(Long mechanicId, String bikeModel, String problem, BigDecimal cost) {
    Mechanic mechanic = mechanics.findById(mechanicId)
        .orElseThrow(() -> new JobNotFoundException("Mechanic " + mechanicId));
    RepairJob job = new RepairJob(bikeModel, problem, cost);
    mechanic.addJob(job);
    return jobs.save(job);
  }

  @Transactional
  public RepairJob complete(Long jobId) {
    RepairJob job = jobs.findById(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job " + jobId));
    job.setStatus(RepairStatus.DONE);      // no save() call: dirty checking writes the UPDATE
    return job;
  }

  @Transactional
  public void receiveAll(Long mechanicId, List<RepairJob> newJobs) {
    for (RepairJob job : newJobs) {
      if (job.getCost().signum() < 0) {
        throw new IllegalArgumentException("Negative cost for " + job.getBikeModel());
      }
      receive(mechanicId, job.getBikeModel(), job.getProblem(), job.getCost());
    }
  }

  public List<RepairJob> findByStatus(RepairStatus status) {
    return jobs.findWithMechanicByStatus(status);
  }

  public RepairJob find(Long jobId) {
    return jobs.findWithMechanic(jobId)
        .orElseThrow(() -> new JobNotFoundException("Job " + jobId));
  }

  // readOnly = true: Hibernate skips dirty checking, so this change is never written
  public void changeCostInReadOnly(Long jobId, BigDecimal cost) {
    jobs.findById(jobId).orElseThrow().setCost(cost);
  }
}
