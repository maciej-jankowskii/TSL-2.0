package com.tsl.repository;

import com.tsl.model.warehouse.order.WarehouseOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseOrderRepository extends CrudRepository<WarehouseOrder, Long> {

    List<WarehouseOrder> findAll();

    @Query("SELECT wo FROM WarehouseOrder wo ORDER BY " +
            "CASE WHEN :sortBy = 'warehouse.id' THEN wo.warehouse.id END ASC, " +
            "CASE WHEN :sortBy = 'customer.id' THEN wo.customer.id END ASC, " +
            "CASE WHEN :sortBy = 'dateAdded' THEN wo.dateAdded END ASC, " +
            "CASE WHEN :sortBy = 'dateOfReturn' THEN wo.dateOfReturn END ASC, " +
            "CASE WHEN :sortBy = 'totalCosts' THEN wo.totalCosts END ASC, " +
            "CASE WHEN :sortBy = 'isCompleted' THEN wo.isCompleted END ASC")
    List<WarehouseOrder> findAllOrderBy(@Param("sortBy") String sortBy);

    List<WarehouseOrder> findByIsCompletedFalse();
}
