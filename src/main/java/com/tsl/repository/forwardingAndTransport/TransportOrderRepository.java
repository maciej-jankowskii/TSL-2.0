package com.tsl.repository.forwardingAndTransport;

import com.tsl.model.order.TransportOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportOrderRepository extends CrudRepository<TransportOrder, Long> {

    List<TransportOrder> findAll();
    List<TransportOrder> findAllByTransportPlannerEmail(String email);

    @Query("SELECT to FROM TransportOrder to " +
            "WHERE to.transportPlanner.email = :email " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'id' THEN to.id END ASC, " +
            "CASE WHEN :sortBy = 'orderNumber' THEN to.orderNumber END ASC, " +
            "CASE WHEN :sortBy = 'dateAdded' THEN to.dateAdded END ASC, " +
            "CASE WHEN :sortBy = 'cargoId' THEN to.cargo.id END ASC, " +
            "CASE WHEN :sortBy = 'transportPlannerId' THEN to.transportPlanner.id END ASC, " +
            "CASE WHEN :sortBy = 'orderStatus' THEN to.orderStatus END ASC, " +
            "CASE WHEN :sortBy = 'truckPlates' THEN to.truck.plates END ASC, " +
            "CASE WHEN :sortBy = 'isInvoiced' THEN to.isInvoiced END ASC ")
    List<TransportOrder> findAllTransportOrdersBy(@Param("email") String email, @Param("sortBy") String sortBy);
}
