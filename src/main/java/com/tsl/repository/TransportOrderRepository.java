package com.tsl.repository;

import com.tsl.model.order.TransportOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportOrderRepository extends CrudRepository<TransportOrder, Long> {

    List<TransportOrder> findAll();
}
