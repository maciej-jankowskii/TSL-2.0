package com.tsl.repository;

import com.tsl.model.order.ForwardingOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForwarderOrderRepository extends CrudRepository<ForwardingOrder, Long> {
    Optional<ForwardingOrder> findById(Long id);
    List<ForwardingOrder> findAll();
    List<ForwardingOrder> findAllByForwarder_Email(String email);
}
