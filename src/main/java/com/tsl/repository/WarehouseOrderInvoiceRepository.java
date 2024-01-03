package com.tsl.repository;

import com.tsl.model.invoice.WarehouseOrderInvoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseOrderInvoiceRepository extends CrudRepository<WarehouseOrderInvoice, Long> {

    List<WarehouseOrderInvoice> findAll();
}
