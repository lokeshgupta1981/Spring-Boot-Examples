package com.howtodoinjava.springboothibernate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Mechanic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "mechanic")
  private List<RepairJob> jobs = new ArrayList<>();

  protected Mechanic() {
  }

  public Mechanic(String name) {
    this.name = name;
  }

  public void addJob(RepairJob job) {
    jobs.add(job);
    job.setMechanic(this);
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<RepairJob> getJobs() {
    return jobs;
  }

  @Override
  public String toString() {
    return name;
  }
}
