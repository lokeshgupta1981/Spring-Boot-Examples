package com.howtodoinjava.springboothibernate;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MechanicRepository extends JpaRepository<Mechanic, Long> {

  Optional<Mechanic> findByName(String name);
}
