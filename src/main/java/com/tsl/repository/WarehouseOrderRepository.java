package com.tsl.repository;

import com.tsl.model.warehouse.order.WarehouseOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseOrderRepository extends CrudRepository<WarehouseOrder, Long> {

    List<WarehouseOrder> findAll();
}
