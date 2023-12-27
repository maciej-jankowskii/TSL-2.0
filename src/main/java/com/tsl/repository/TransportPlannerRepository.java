package com.tsl.repository;

import com.tsl.model.employee.Forwarder;
import com.tsl.model.employee.TransportPlanner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportPlannerRepository extends CrudRepository<TransportPlanner, Long> {

    Boolean existsByEmail(String email);
    List<TransportPlanner> findAll();
    Optional<TransportPlanner> findByEmail(String email);
}
