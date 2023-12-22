package com.tsl.repository;

import com.tsl.model.employee.Forwarder;
import com.tsl.model.employee.TransportPlanner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportPlannerRepository extends CrudRepository<TransportPlanner, Long> {

    Boolean existsByEmail(String email);
    List<TransportPlanner> findAll();
}
