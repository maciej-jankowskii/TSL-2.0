package com.tsl.repository;

import com.tsl.model.employee.Forwarder;
import com.tsl.model.employee.WarehouseWorker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseWorkerRepository extends CrudRepository<WarehouseWorker, Long> {
    Optional<WarehouseWorker> findById(Long id);
    List<WarehouseWorker> findAll();
    Boolean existsByEmail(String email);

}
