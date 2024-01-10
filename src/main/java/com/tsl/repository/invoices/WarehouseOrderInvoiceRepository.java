package com.tsl.repository.invoices;

import com.tsl.model.invoice.WarehouseOrderInvoice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseOrderInvoiceRepository extends CrudRepository<WarehouseOrderInvoice, Long> {

    List<WarehouseOrderInvoice> findAll();

    @Query("SELECT wi FROM WarehouseOrderInvoice wi ORDER BY " +
            "CASE WHEN :sortBy = 'invoiceNumber' THEN wi.invoiceNumber END ASC, " +
            "CASE WHEN :sortBy = 'invoiceDate' THEN wi.invoiceDate END ASC, " +
            "CASE WHEN :sortBy = 'dueDate' THEN wi.dueDate END ASC, " +
            "CASE WHEN :sortBy = 'isPaid' THEN wi.isPaid END ASC, " +
            "CASE WHEN :sortBy = 'warehouseOrderId' THEN wi.warehouseOrder.id END ASC")
    List<WarehouseOrderInvoice> findAllWarehouseInvoicesBy(@Param("sortBy") String sortBy);
}
