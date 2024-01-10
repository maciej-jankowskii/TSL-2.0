package com.tsl.repository.forwardingAndTransport;

import com.tsl.model.order.ForwardingOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForwarderOrderRepository extends CrudRepository<ForwardingOrder, Long> {
    Optional<ForwardingOrder> findById(Long id);
    List<ForwardingOrder> findAll();
    List<ForwardingOrder> findAllByForwarder_Email(String email);

    @Query("SELECT fo FROM ForwardingOrder fo " +
            "WHERE fo.forwarder.email = :email " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'id' THEN fo.id END ASC, " +
            "CASE WHEN :sortBy = 'dateAdded' THEN fo.dateAdded END ASC, " +
            "CASE WHEN :sortBy = 'carrier' THEN fo.carrier.fullName END ASC, " +
            "CASE WHEN :sortBy = 'margin' THEN fo.margin END ASC, " +
            "CASE WHEN :sortBy = 'orderStatus' THEN fo.orderStatus END ASC, " +
            "CASE WHEN :sortBy = 'isInvoiced' THEN fo.isInvoiced END ASC ")
    List<ForwardingOrder> findAllForwardingOrdersBy(@Param("email") String email, @Param("sortBy") String sortBy);
}
