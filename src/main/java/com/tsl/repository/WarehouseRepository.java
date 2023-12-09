package com.tsl.repository;

import com.tsl.model.warehouse.Warehouse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    List<Warehouse> findAll();
}
