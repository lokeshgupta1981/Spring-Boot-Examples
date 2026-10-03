package com.howtodoinjava.springboothibernate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RepairJobRepository extends JpaRepository<RepairJob, Long> {

  // Derived queries: Spring Data builds the JPQL from the method name
  List<RepairJob> findByStatus(RepairStatus status);

  List<RepairJob> findByMechanicNameAndStatusOrderByCostDesc(String name, RepairStatus status);

  long countByStatus(RepairStatus status);

  boolean existsByBikeModelAndStatusNot(String bikeModel, RepairStatus status);

  // @Query: we write the JPQL ourselves
  @Query("select j from RepairJob j where j.cost >= :min order by j.cost desc")
  List<RepairJob> findCostingAtLeast(BigDecimal min);

  @Query("select j from RepairJob j join fetch j.mechanic where j.status = :status order by j.id")
  List<RepairJob> findWithMechanicByStatus(RepairStatus status);

  @Query("select j from RepairJob j join fetch j.mechanic where j.id = :id")
  Optional<RepairJob> findWithMechanic(Long id);

  @Query("""
      select new com.howtodoinjava.springboothibernate.MechanicRevenue(m.name, sum(j.cost))
      from RepairJob j join j.mechanic m
      where j.status = DONE
      group by m.name
      order by m.name""")
  List<MechanicRevenue> revenuePerMechanic();

  // Jakarta Persistence 3.2 JPQL: the || operator concatenates strings
  @Query("select j.bikeModel || ': ' || j.problem from RepairJob j order by j.id")
  List<String> ticketLabels();

  @Modifying
  @Query("update RepairJob j set j.status = :to where j.status = :from")
  int moveAll(RepairStatus from, RepairStatus to);
}
