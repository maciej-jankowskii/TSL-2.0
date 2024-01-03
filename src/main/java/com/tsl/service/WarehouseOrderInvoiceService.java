package com.tsl.service;

import com.tsl.dtos.WarehouseOrderInvoiceDTO;
import com.tsl.exceptions.InvoiceNotFoundException;
import com.tsl.exceptions.OrderNotFoundException;
import com.tsl.mapper.WarehouseOrderInvoiceMapper;
import com.tsl.model.contractor.Customer;
import com.tsl.model.invoice.WarehouseOrderInvoice;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.WarehouseOrderInvoiceRepository;
import com.tsl.repository.WarehouseOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseOrderInvoiceService {
    private final static long PAYMENT_DATE_FOR_INVOICE = 30;
    private final WarehouseOrderInvoiceMapper warehouseOrderInvoiceMapper;
    private final WarehouseOrderInvoiceRepository warehouseOrderInvoiceRepository;
    private final WarehouseOrderRepository warehouseOrderRepository;
    private final VatCalculatorService vatCalculatorService;

    public WarehouseOrderInvoiceService(WarehouseOrderInvoiceMapper warehouseOrderInvoiceMapper, WarehouseOrderInvoiceRepository warehouseOrderInvoiceRepository, WarehouseOrderRepository warehouseOrderRepository, VatCalculatorService vatCalculatorService) {
        this.warehouseOrderInvoiceMapper = warehouseOrderInvoiceMapper;
        this.warehouseOrderInvoiceRepository = warehouseOrderInvoiceRepository;
        this.warehouseOrderRepository = warehouseOrderRepository;
        this.vatCalculatorService = vatCalculatorService;
    }

    public List<WarehouseOrderInvoiceDTO> findAllWarehouseInvoices(){
        return warehouseOrderInvoiceRepository.findAll().stream().map(warehouseOrderInvoiceMapper::mapToDTO).collect(Collectors.toList());
    }
    public WarehouseOrderInvoiceDTO findWarehouseInvoiceById(Long id) {
        return warehouseOrderInvoiceRepository.findById(id).map(warehouseOrderInvoiceMapper::mapToDTO).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
    }

    @Transactional
    public WarehouseOrderInvoiceDTO addWarehouseInvoice(WarehouseOrderInvoiceDTO invoiceDTO){
        WarehouseOrderInvoice invoice = warehouseOrderInvoiceMapper.mapToEntity(invoiceDTO);

        WarehouseOrder order = extractOrderFromInvoice(invoiceDTO);
        Customer customer = extractCustomerFromOrder(order);

        changeCompletedStatusForOrder(order);

        BigDecimal grossValue = vatCalculatorService.calculateGrossValue(BigDecimal.valueOf(order.getTotalCosts()), customer.getVatNumber());

        addAdditionalDataForInvoiceAndCustomer(invoice, order, customer, grossValue);

        WarehouseOrderInvoice saved = warehouseOrderInvoiceRepository.save(invoice);
        return warehouseOrderInvoiceMapper.mapToDTO(saved);
    }

    private static void addAdditionalDataForInvoiceAndCustomer(WarehouseOrderInvoice invoice, WarehouseOrder order, Customer customer, BigDecimal grossValue) {
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(PAYMENT_DATE_FOR_INVOICE));
        invoice.setNetValue(BigDecimal.valueOf(order.getTotalCosts()));
        invoice.setGrossValue(grossValue);
        invoice.setIsPaid(false);

        customer.setBalance(customer.getBalance().add(grossValue));
    }

    private static void changeCompletedStatusForOrder(WarehouseOrder order) {
        order.setIsCompleted(true);
    }

    private static Customer extractCustomerFromOrder(WarehouseOrder order) {
        Customer customer = order.getCustomer();
        if (customer == null){
            throw new EntityNotFoundException("Customer not found");
        }
        return customer;
    }

    private WarehouseOrder extractOrderFromInvoice(WarehouseOrderInvoiceDTO invoiceDTO) {
        Long orderId = invoiceDTO.getWarehouseOrderId();
        return warehouseOrderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Warehouse order not found"));
    }
}
