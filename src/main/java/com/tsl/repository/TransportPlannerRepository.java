package com.tsl.repository;

import com.tsl.model.employee.TransportPlanner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportPlannerRepository extends CrudRepository<TransportPlanner, Long> {
}
