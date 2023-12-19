package com.tsl.repository;

import com.tsl.model.warehouse.Warehouse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    List<Warehouse> findAll();
    Optional<Warehouse> findById(Long id);

    @Query("SELECT w FROM Warehouse w ORDER BY " +
            "CASE WHEN :sortBy = 'typeOfGoods' THEN w.typeOfGoods END ASC, " +
            "CASE WHEN :sortBy = 'costPer100SquareMeters' THEN w.costPer100SquareMeters END ASC, " +
            "CASE WHEN :sortBy = 'availableArea' THEN w.availableArea END ASC")
    List<Warehouse> findAllOrderBy(@Param("sortBy") String sortBy);
}
