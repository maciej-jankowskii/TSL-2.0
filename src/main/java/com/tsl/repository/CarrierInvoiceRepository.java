package com.tsl.repository;

import com.tsl.model.contractor.Carrier;
import com.tsl.model.invoice.CarrierInvoice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierInvoiceRepository extends CrudRepository<CarrierInvoice, Long> {

    List<CarrierInvoice> findAll();
    Optional<CarrierInvoice> findById(Long id);

    @Query("SELECT ci FROM CarrierInvoice ci ORDER BY " +
            "CASE WHEN :sortBy = 'invoiceNumber' THEN ci.invoiceNumber END ASC, " +
            "CASE WHEN :sortBy = 'invoiceDate' THEN ci.invoiceDate END ASC, " +
            "CASE WHEN :sortBy = 'dueDate' THEN ci.dueDate END ASC, " +
            "CASE WHEN :sortBy = 'isPaid' THEN ci.isPaid END ASC, " +
            "CASE WHEN :sortBy = 'carrierFullName' THEN ci.carrier.fullName END ASC, " +
            "CASE WHEN :sortBy = 'orderId' THEN ci.order.id END ASC")
    List<CarrierInvoice> findAllCarrierInvoicesBy(@Param("sortBy") String sortBy);

}
