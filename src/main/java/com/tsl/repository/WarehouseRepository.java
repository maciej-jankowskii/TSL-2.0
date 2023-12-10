package com.tsl.repository;

import com.tsl.model.warehouse.Warehouse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    List<Warehouse> findAll();
    Optional<Warehouse> findById(Long id);
}
