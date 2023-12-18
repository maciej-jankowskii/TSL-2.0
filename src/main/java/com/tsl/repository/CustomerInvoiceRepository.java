package com.tsl.repository;

import com.tsl.model.invoice.CustomerInvoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerInvoiceRepository extends CrudRepository<CustomerInvoice, Long> {
    List<CustomerInvoice> findAll();
    Optional<CustomerInvoice> findById(Long id);
}
