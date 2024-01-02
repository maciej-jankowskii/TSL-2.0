package com.tsl.repository;

import com.tsl.model.invoice.CarrierInvoice;
import com.tsl.model.invoice.CustomerInvoice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerInvoiceRepository extends CrudRepository<CustomerInvoice, Long> {
    List<CustomerInvoice> findAll();
    Optional<CustomerInvoice> findById(Long id);

    @Query("SELECT ci FROM CustomerInvoice ci ORDER BY " +
            "CASE WHEN :sortBy = 'invoiceNumber' THEN ci.invoiceNumber END ASC, " +
            "CASE WHEN :sortBy = 'invoiceDate' THEN ci.invoiceDate END ASC, " +
            "CASE WHEN :sortBy = 'dueDate' THEN ci.dueDate END ASC, " +
            "CASE WHEN :sortBy = 'isPaid' THEN ci.isPaid END ASC, " +
            "CASE WHEN :sortBy = 'customerFullName' THEN ci.customer.fullName END ASC, " +
            "CASE WHEN :sortBy = 'cargoId' THEN ci.cargo.id END ASC")
    List<CustomerInvoice> findAllCustomerInvoicesBy(@Param("sortBy") String sortBy);
}
