package com.tsl.service.invoices;

import com.tsl.dtos.invoices.WarehouseOrderInvoiceDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.WarehouseOrderInvoiceMapper;
import com.tsl.model.contractor.Customer;
import com.tsl.model.invoice.WarehouseOrderInvoice;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.invoices.WarehouseOrderInvoiceRepository;
import com.tsl.repository.warehouses.WarehouseOrderRepository;
import com.tsl.service.calculators.VatCalculatorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseOrderInvoiceService {
    private final static long PAYMENT_DATE_FOR_INVOICE = 30;
    private final WarehouseOrderInvoiceMapper warehouseOrderInvoiceMapper;
    private final WarehouseOrderInvoiceRepository warehouseOrderInvoiceRepository;
    private final WarehouseOrderRepository warehouseOrderRepository;
    private final VatCalculatorService vatCalculatorService;


    /***
     Finding methods
     */

    public List<WarehouseOrderInvoiceDTO> findAllWarehouseInvoices() {
        return warehouseOrderInvoiceRepository.findAll().stream().map(warehouseOrderInvoiceMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public WarehouseOrderInvoiceDTO findWarehouseInvoiceById(Long id) {
        return warehouseOrderInvoiceRepository.findById(id).map(warehouseOrderInvoiceMapper::mapToDTO)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
    }

    public List<WarehouseOrderInvoiceDTO> findAllWarehouseInvoicesSortedBy(String sortBy) {
        return warehouseOrderInvoiceRepository.findAllWarehouseInvoicesBy(sortBy).stream()
                .map(warehouseOrderInvoiceMapper::mapToDTO).collect(Collectors.toList());
    }

    /***
     Create and update methods
     */

    @Transactional
    public WarehouseOrderInvoiceDTO addWarehouseInvoice(WarehouseOrderInvoiceDTO invoiceDTO) {
        WarehouseOrderInvoice invoice = warehouseOrderInvoiceMapper.mapToEntity(invoiceDTO);

        WarehouseOrder order = warehouseOrderRepository.findById(invoiceDTO.getWarehouseOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Warehouse order not found"));
        Customer customer = extractCustomerFromOrder(order);

        changeCompletedStatusForOrder(order);

        BigDecimal grossValue = vatCalculatorService.calculateGrossValue(BigDecimal.valueOf(order.getTotalCosts()), customer.getVatNumber());

        addAdditionalDataForInvoiceAndCustomer(invoice, order, customer, grossValue);

        WarehouseOrderInvoice saved = warehouseOrderInvoiceRepository.save(invoice);
        return warehouseOrderInvoiceMapper.mapToDTO(saved);
    }

    @Transactional
    public WarehouseOrderInvoiceDTO markInvoiceAsPaid(Long invoiceId) {
        WarehouseOrderInvoice invoice = warehouseOrderInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));

        checkingIsPaidInvoice(invoice);
        changeCustomerBalance(invoice);

        WarehouseOrderInvoice saved = warehouseOrderInvoiceRepository.save(invoice);
        return warehouseOrderInvoiceMapper.mapToDTO(saved);
    }

    @Transactional
    public void updateWarehouseInvoice(WarehouseOrderInvoiceDTO currentDTO, WarehouseOrderInvoiceDTO updatedDTO) {
        WarehouseOrderInvoice invoice = warehouseOrderInvoiceMapper.mapToEntity(updatedDTO);

        checkingPaidStatus(invoice);
        checkingUnauthorizedValueChange(currentDTO, updatedDTO);

        warehouseOrderInvoiceRepository.save(invoice);
    }

    /***
     Helper methods
     */

    private static void checkingUnauthorizedValueChange(WarehouseOrderInvoiceDTO currentDTO, WarehouseOrderInvoiceDTO updatedDTO) {
        if (currentDTO.getIsPaid() == true && updatedDTO.getIsPaid() == false) {
            throw new CannotEditEntityException("Cannot change isPaid value from paid to false");
        }
    }

    private static void checkingPaidStatus(WarehouseOrderInvoice invoice) {
        if (invoice.getIsPaid()) {
            throw new CannotEditEntityException("Cannot edit customer invoice because is paid.");
        }
    }

    private static void checkingIsPaidInvoice(WarehouseOrderInvoice invoice) {
        if (invoice.getIsPaid()) {
            throw new InvoiceAlreadyPaidException("Invoice is already paid");
        }
        invoice.setIsPaid(true);
    }

    private static void changeCustomerBalance(WarehouseOrderInvoice invoice) {
        Customer customer = invoice.getWarehouseOrder().getCustomer();
        BigDecimal balance = customer.getBalance();
        BigDecimal grossValue = invoice.getGrossValue();
        customer.setBalance(balance.subtract(grossValue));
    }

    private static void addAdditionalDataForInvoiceAndCustomer(WarehouseOrderInvoice invoice, WarehouseOrder order,
                                                               Customer customer, BigDecimal grossValue) {
        invoice.setWarehouseOrder(order);
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
        if (customer == null) {
            throw new EntityNotFoundException("Customer not found");
        }
        return customer;
    }
}
