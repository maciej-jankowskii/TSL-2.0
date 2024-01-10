package com.tsl.service;

import com.tsl.dtos.WarehouseOrderInvoiceDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.WarehouseOrderInvoiceMapper;
import com.tsl.model.contractor.Customer;
import com.tsl.model.invoice.WarehouseOrderInvoice;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.WarehouseOrderInvoiceRepository;
import com.tsl.repository.WarehouseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WarehouseOrderInvoiceServiceTest {

    @Mock private WarehouseOrderInvoiceMapper warehouseOrderInvoiceMapper;
    @Mock private WarehouseOrderInvoiceRepository warehouseOrderInvoiceRepository;
    @Mock private WarehouseOrderRepository warehouseOrderRepository;
    @Mock private VatCalculatorService vatCalculatorService;

    @InjectMocks private WarehouseOrderInvoiceService warehouseOrderInvoiceService;
    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Warehouse Invoices successfully")
    public void testFindAllWarehouseInvoices_Success() {
        WarehouseOrderInvoice invoice1 = prepareWarehouseOrderInvoice();
        WarehouseOrderInvoice invoice2 = prepareWarehouseOrderInvoice();
        WarehouseOrderInvoiceDTO invoiceDTO1 = prepareWarehouseOrderInvoiceDTO();
        WarehouseOrderInvoiceDTO invoiceDTO2 = prepareWarehouseOrderInvoiceDTO();

        when(warehouseOrderInvoiceRepository.findAll()).thenReturn(Arrays.asList(invoice1, invoice2));
        when(warehouseOrderInvoiceMapper.mapToDTO(invoice1)).thenReturn(invoiceDTO1);
        when(warehouseOrderInvoiceMapper.mapToDTO(invoice2)).thenReturn(invoiceDTO2);

        List<WarehouseOrderInvoiceDTO> resultInvoices = warehouseOrderInvoiceService.findAllWarehouseInvoices();

        assertNotNull(resultInvoices);
        assertEquals(2, resultInvoices.size());

        verify(warehouseOrderInvoiceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Warehouse Invoice by ID successfully")
    public void testFindWarehouseInvoiceById_Success() {
        Long invoiceId = 1L;
        WarehouseOrderInvoice invoice = prepareWarehouseOrderInvoice();
        WarehouseOrderInvoiceDTO invoiceDTO = prepareWarehouseOrderInvoiceDTO();

        when(warehouseOrderInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(warehouseOrderInvoiceMapper.mapToDTO(invoice)).thenReturn(invoiceDTO);

        WarehouseOrderInvoiceDTO resultInvoice = warehouseOrderInvoiceService.findWarehouseInvoiceById(invoiceId);

        assertNotNull(resultInvoice);
        assertEquals("12345", resultInvoice.getInvoiceNumber());

        verify(warehouseOrderInvoiceRepository, times(1)).findById(invoiceId);
    }

    @Test
    @DisplayName("Should throw InvoiceNotFoundException when Warehouse Invoice is not found by ID")
    public void testFindWarehouseInvoiceById_InvoiceNotFound() {
        Long invoiceId = 1L;

        when(warehouseOrderInvoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> warehouseOrderInvoiceService.findWarehouseInvoiceById(invoiceId));

        verify(warehouseOrderInvoiceMapper, never()).mapToDTO(any());
    }

    @Test
    @DisplayName("Should add Warehouse Invoice successfully")
    public void testAddWarehouseInvoice_Success() {
        WarehouseOrderInvoiceDTO invoiceDTO = prepareWarehouseOrderInvoiceDTO();
        WarehouseOrderInvoice invoice = prepareWarehouseOrderInvoice();
        WarehouseOrder order = prepareWarehouseOrder();
        Customer customer = prepareCustomer();

        when(warehouseOrderInvoiceMapper.mapToEntity(invoiceDTO)).thenReturn(invoice);
        when(warehouseOrderRepository.findById(invoiceDTO.getWarehouseOrderId())).thenReturn(Optional.of(order));
        when(vatCalculatorService.calculateGrossValue(BigDecimal.valueOf(order.getTotalCosts()), customer.getVatNumber())).thenReturn(BigDecimal.TEN);
        when(warehouseOrderInvoiceRepository.save(invoice)).thenReturn(invoice);
        when(warehouseOrderInvoiceMapper.mapToDTO(invoice)).thenReturn(invoiceDTO);

        WarehouseOrderInvoiceDTO result = warehouseOrderInvoiceService.addWarehouseInvoice(invoiceDTO);

        assertNotNull(result);
        assertEquals("12345", result.getInvoiceNumber());

        verify(warehouseOrderInvoiceRepository, times(1)).save(invoice);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when Warehouse Order is not found during adding Warehouse Invoice")
    public void testAddWarehouseInvoice_OrderNotFound() {
        WarehouseOrderInvoiceDTO invoiceDTO = prepareWarehouseOrderInvoiceDTO();

        when(warehouseOrderInvoiceMapper.mapToEntity(invoiceDTO)).thenReturn(new WarehouseOrderInvoice());
        when(warehouseOrderRepository.findById(invoiceDTO.getWarehouseOrderId())).thenReturn(Optional.empty());

        assertThrows(WarehouseOrderNotFoundException.class, () -> warehouseOrderInvoiceService.addWarehouseInvoice(invoiceDTO));

        verify(vatCalculatorService, never()).calculateGrossValue(any(), any());
        verify(warehouseOrderInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvoiceNotFoundException when Warehouse Invoice is not found during marking as Paid")
    public void testMarkInvoiceAsPaid_InvoiceNotFound() {
        Long invoiceId = 1L;

        when(warehouseOrderInvoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> warehouseOrderInvoiceService.markInvoiceAsPaid(invoiceId));

        verify(warehouseOrderInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvoiceAlreadyPaidException when Warehouse Invoice is already paid during marking as Paid")
    public void testMarkInvoiceAsPaid_AlreadyPaid() {
        Long invoiceId = 1L;
        WarehouseOrderInvoice invoice = preparePaidWarehouseOrderInvoice();

        when(warehouseOrderInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThrows(InvoiceAlreadyPaidException.class, () -> warehouseOrderInvoiceService.markInvoiceAsPaid(invoiceId));

        verify(warehouseOrderInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Warehouse Invoice successfully")
    public void testUpdateWarehouseInvoice_Success() {
        WarehouseOrderInvoiceDTO currentDTO = prepareWarehouseOrderInvoiceDTO();
        WarehouseOrderInvoiceDTO updatedDTO = prepareWarehouseOrderInvoiceDTO();
        WarehouseOrderInvoice invoice = prepareWarehouseOrderInvoice();

        when(warehouseOrderInvoiceMapper.mapToEntity(updatedDTO)).thenReturn(invoice);

        assertDoesNotThrow(() -> warehouseOrderInvoiceService.updateWarehouseInvoice(currentDTO, updatedDTO));

        verify(warehouseOrderInvoiceRepository, times(1)).save(invoice);
    }

    @Test
    @DisplayName("Should throw CannotEditEntityException when trying to change isPaid value from true to false during update")
    public void testUpdateWarehouseInvoice_CannotEditIsPaid() {
        WarehouseOrderInvoiceDTO currentDTO = preparePaidWarehouseOrderInvoiceDTO();
        WarehouseOrderInvoiceDTO updatedDTO = prepareWarehouseOrderInvoiceDTO();
        WarehouseOrderInvoice invoice = prepareWarehouseOrderInvoice();
        currentDTO.setIsPaid(true);
        updatedDTO.setIsPaid(false);

        when(warehouseOrderInvoiceMapper.mapToEntity(updatedDTO)).thenReturn(invoice);
        assertThrows(CannotEditEntityException.class, () -> warehouseOrderInvoiceService.updateWarehouseInvoice(currentDTO, updatedDTO));

        verify(warehouseOrderInvoiceRepository, never()).save(any());
    }

    private WarehouseOrderInvoiceDTO prepareWarehouseOrderInvoiceDTO() {
        WarehouseOrderInvoiceDTO invoiceDTO = new WarehouseOrderInvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber("12345");
        invoiceDTO.setWarehouseOrderId(2L);
        invoiceDTO.setIsPaid(false);
        return invoiceDTO;
    }

    private WarehouseOrderInvoiceDTO preparePaidWarehouseOrderInvoiceDTO() {
        WarehouseOrderInvoiceDTO invoiceDTO = new WarehouseOrderInvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber("12345");
        invoiceDTO.setWarehouseOrderId(2L);
        invoiceDTO.setIsPaid(true);
        return invoiceDTO;
    }

    private WarehouseOrderInvoice prepareWarehouseOrderInvoice() {
        WarehouseOrderInvoice invoice = new WarehouseOrderInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("12345");
        WarehouseOrder order = new WarehouseOrder();
        order.setId(1L);
        order.setWarehouse(new Warehouse());
        Customer customer = new Customer();
        customer.setBalance(BigDecimal.valueOf(10000));
        order.setCustomer(customer);
        invoice.setWarehouseOrder(order);
        invoice.setIsPaid(false);
        invoice.setNetValue(BigDecimal.valueOf(1000));
        invoice.setGrossValue(BigDecimal.valueOf(1230));
        return invoice;
    }

    private WarehouseOrderInvoice preparePaidWarehouseOrderInvoice() {
        WarehouseOrderInvoice invoice = new WarehouseOrderInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("12345");
        WarehouseOrder order = new WarehouseOrder();
        order.setId(1L);
        order.setWarehouse(new Warehouse());
        invoice.setWarehouseOrder(order);
        invoice.setIsPaid(true);
        return invoice;
    }

    private WarehouseOrder prepareWarehouseOrder() {
        WarehouseOrder order = new WarehouseOrder();
        order.setId(2L);
        order.setTotalCosts(1000.0);
        order.setIsCompleted(false);

        Customer customer = new Customer();
        customer.setId(3L);
        customer.setVatNumber("PL123456789");
        customer.setBalance(BigDecimal.ZERO);

        order.setCustomer(customer);
        return order;
    }

    private Customer prepareCustomer() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setVatNumber("PL123456789");
        customer.setBalance(BigDecimal.ZERO);
        return customer;
    }

}