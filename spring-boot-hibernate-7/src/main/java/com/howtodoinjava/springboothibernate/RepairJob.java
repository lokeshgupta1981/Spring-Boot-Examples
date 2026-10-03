package com.howtodoinjava.springboothibernate;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
public class RepairJob {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String bikeModel;

  private String problem;

  @Enumerated(EnumType.STRING)
  private RepairStatus status = RepairStatus.RECEIVED;

  @Column(precision = 8, scale = 2, check = @CheckConstraint(constraint = "cost >= 0"))
  private BigDecimal cost;

  @CreationTimestamp
  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mechanic_id")
  private Mechanic mechanic;

  protected RepairJob() {
  }

  public RepairJob(String bikeModel, String problem, BigDecimal cost) {
    this.bikeModel = bikeModel;
    this.problem = problem;
    this.cost = cost;
  }

  public Long getId() {
    return id;
  }

  public String getBikeModel() {
    return bikeModel;
  }

  public String getProblem() {
    return problem;
  }

  public RepairStatus getStatus() {
    return status;
  }

  public void setStatus(RepairStatus status) {
    this.status = status;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Mechanic getMechanic() {
    return mechanic;
  }

  void setMechanic(Mechanic mechanic) {
    this.mechanic = mechanic;
  }

  @Override
  public String toString() {
    return bikeModel + " (" + problem + ", " + status + ", " + cost + ")";
  }
}
