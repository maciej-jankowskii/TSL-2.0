package com.tsl.repository;

import com.tsl.model.invoice.CarrierInvoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierInvoiceRepository extends CrudRepository<CarrierInvoice, Long> {

    List<CarrierInvoice> findAll();
    Optional<CarrierInvoice> findById(Long id);

}
