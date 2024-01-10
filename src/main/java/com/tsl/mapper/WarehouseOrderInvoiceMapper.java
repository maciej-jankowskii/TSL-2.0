package com.tsl.mapper;

import com.tsl.dtos.WarehouseOrderInvoiceDTO;
import com.tsl.exceptions.NullEntityException;
import com.tsl.exceptions.WarehouseOrderNotFoundException;
import com.tsl.model.invoice.WarehouseOrderInvoice;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.WarehouseOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class WarehouseOrderInvoiceMapper {
    private final WarehouseOrderRepository warehouseOrderRepository;

    public WarehouseOrderInvoiceMapper(WarehouseOrderRepository warehouseOrderRepository) {
        this.warehouseOrderRepository = warehouseOrderRepository;
    }

    public WarehouseOrderInvoice mapToEntity(WarehouseOrderInvoiceDTO invoiceDTO){
        if (invoiceDTO == null){
            throw new NullEntityException("Warehouse order invoice data cannot be null");
        }

        WarehouseOrderInvoice invoice = new WarehouseOrderInvoice();
        invoice.setId(invoiceDTO.getId());
        invoice.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setDueDate(invoiceDTO.getDueDate());
        invoice.setNetValue(invoiceDTO.getNetValue());
        invoice.setGrossValue(invoiceDTO.getGrossValue());
        invoice.setIsPaid(invoiceDTO.getIsPaid());
        return invoice;
    }

    public WarehouseOrderInvoiceDTO mapToDTO(WarehouseOrderInvoice invoice){
        if (invoice == null){
            throw new NullEntityException("Invoice cannot be null");
        }
        WarehouseOrderInvoiceDTO dto = new WarehouseOrderInvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setNetValue(invoice.getNetValue());
        dto.setGrossValue(invoice.getGrossValue());
        dto.setIsPaid(invoice.getIsPaid());
        dto.setWarehouseOrderId(invoice.getWarehouseOrder().getId());
        return dto;
    }
}
