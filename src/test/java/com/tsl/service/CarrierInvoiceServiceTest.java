package com.tsl.service;

import com.tsl.dtos.invoices.CarrierInvoiceDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.CarrierInvoiceMapper;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.invoice.CarrierInvoice;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.repository.invoices.CarrierInvoiceRepository;
import com.tsl.repository.forwardingAndTransport.CarrierRepository;
import com.tsl.repository.forwardingAndTransport.ForwarderOrderRepository;
import com.tsl.service.calculators.VatCalculatorService;
import com.tsl.service.invoices.CarrierInvoiceService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarrierInvoiceServiceTest {

    @Mock
    private CarrierInvoiceRepository carrierInvoiceRepository;
    @Mock
    private CarrierInvoiceMapper carrierInvoiceMapper;
    @Mock
    private VatCalculatorService vatCalculatorService;
    @Mock
    private ForwarderOrderRepository forwarderOrderRepository;
    @Mock
    private CarrierRepository carrierRepository;
    @InjectMocks
    private CarrierInvoiceService carrierInvoiceService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Carrier Invoices successfully")
    public void testFindAllCarrierInvoices_Success() {
        CarrierInvoice invoice1 = prepareCarrierInvoice();
        CarrierInvoice invoice2 = prepareCarrierInvoice();
        CarrierInvoiceDTO invoiceDTO1 = prepareCarrierInvoiceDTO();
        CarrierInvoiceDTO invoiceDTO2 = prepareCarrierInvoiceDTO();

        when(carrierInvoiceRepository.findAll()).thenReturn(Arrays.asList(invoice1, invoice2));
        when(carrierInvoiceMapper.mapToDTO(invoice1)).thenReturn(invoiceDTO1);
        when(carrierInvoiceMapper.mapToDTO(invoice2)).thenReturn(invoiceDTO2);

        List<CarrierInvoiceDTO> resultInvoices = carrierInvoiceService.findAll();

        assertNotNull(resultInvoices);
        assertEquals(2, resultInvoices.size());

        verify(carrierInvoiceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Carrier Invoice by ID successfully")
    public void testFindCarrierInvoiceById_Success() {
        Long invoiceId = 1L;
        CarrierInvoice invoice = prepareCarrierInvoice();
        CarrierInvoiceDTO invoiceDTO = prepareCarrierInvoiceDTO();

        when(carrierInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(carrierInvoiceMapper.mapToDTO(invoice)).thenReturn(invoiceDTO);

        CarrierInvoiceDTO resultInvoice = carrierInvoiceService.findCarrierInvoiceById(invoiceId);

        assertNotNull(resultInvoice);
        assertEquals("12345", resultInvoice.getInvoiceNumber());

        verify(carrierInvoiceRepository, times(1)).findById(invoiceId);
    }

    @Test
    @DisplayName("Should throw InvoiceNotFoundException when Carrier Invoice is not found by ID")
    public void testFindCarrierInvoiceById_InvoiceNotFound() {
        Long invoiceId = 1L;

        when(carrierInvoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> carrierInvoiceService.findCarrierInvoiceById(invoiceId));

        verify(carrierInvoiceMapper, never()).mapToDTO(any());
    }

    @Test
    @DisplayName("Should add Carrier Invoice successfully")
    public void testAddCarrierInvoice_Success() {
        CarrierInvoiceDTO invoiceDTO = prepareCarrierInvoiceDTO();
        CarrierInvoice invoice = prepareCarrierInvoice();
        ForwardingOrder order = prepareForwardingOrder();
        Carrier carrier = prepareCarrier();

        when(carrierInvoiceMapper.mapToEntity(invoiceDTO)).thenReturn(invoice);
        when(forwarderOrderRepository.findById(invoiceDTO.getOrderId())).thenReturn(Optional.of(order));
        when(carrierRepository.findById(invoiceDTO.getCarrierId())).thenReturn(Optional.of(carrier));
        when(vatCalculatorService.calculateGrossValue(order.getPrice(), carrier.getVatNumber())).thenReturn(BigDecimal.TEN);
        when(carrierInvoiceRepository.save(invoice)).thenReturn(invoice);
        when(carrierInvoiceMapper.mapToDTO(invoice)).thenReturn(invoiceDTO);

        CarrierInvoiceDTO result = carrierInvoiceService.addCarrierInvoice(invoiceDTO);

        assertNotNull(result);
        assertEquals("12345", result.getInvoiceNumber());

        verify(carrierInvoiceRepository, times(1)).save(invoice);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when Forwarding Order is not found during adding Carrier Invoice")
    public void testAddCarrierInvoice_OrderNotFound() {
        CarrierInvoiceDTO invoiceDTO = prepareCarrierInvoiceDTO();

        when(carrierInvoiceMapper.mapToEntity(invoiceDTO)).thenReturn(new CarrierInvoice());
        when(forwarderOrderRepository.findById(invoiceDTO.getOrderId())).thenReturn(Optional.empty());
        when(carrierRepository.findById(invoiceDTO.getCarrierId())).thenReturn(Optional.of(new Carrier()));

        assertThrows(OrderNotFoundException.class, () -> carrierInvoiceService.addCarrierInvoice(invoiceDTO));

        verify(vatCalculatorService, never()).calculateGrossValue(any(), any());
        verify(carrierInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CarrierNotFoundException when Carrier is not found during adding Carrier Invoice")
    public void testAddCarrierInvoice_CarrierNotFound() {
        CarrierInvoiceDTO invoiceDTO = prepareCarrierInvoiceDTO();
        ForwardingOrder order = prepareForwardingOrder();

        when(forwarderOrderRepository.findById(invoiceDTO.getOrderId())).thenReturn(Optional.of(order));
        when(carrierRepository.findById(invoiceDTO.getCarrierId())).thenReturn(Optional.empty());

        assertThrows(CarrierNotFoundException.class, () -> carrierInvoiceService.addCarrierInvoice(invoiceDTO));

        verify(vatCalculatorService, never()).calculateGrossValue(any(), any());
        verify(carrierInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should mark Carrier Invoice as Paid successfully")
    public void testMarkInvoiceAsPaid_Success() {
        Long invoiceId = 1L;
        CarrierInvoice invoice = prepareCarrierInvoice();

        when(carrierInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertDoesNotThrow(() -> carrierInvoiceService.markInvoiceAsPaid(invoiceId));

        assertTrue(invoice.getIsPaid());

        verify(carrierInvoiceRepository, times(1)).save(invoice);
    }

    @Test
    @DisplayName("Should throw InvoiceNotFoundException when Carrier Invoice is not found during marking as Paid")
    public void testMarkInvoiceAsPaid_InvoiceNotFound() {
        Long invoiceId = 1L;

        when(carrierInvoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> carrierInvoiceService.markInvoiceAsPaid(invoiceId));

        verify(carrierInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvoiceAlreadyPaidException when Carrier Invoice is already paid during marking as Paid")
    public void testMarkInvoiceAsPaid_AlreadyPaid() {
        Long invoiceId = 1L;
        CarrierInvoice invoice = preparePaidCarrierInvoice();

        when(carrierInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThrows(InvoiceAlreadyPaidException.class, () -> carrierInvoiceService.markInvoiceAsPaid(invoiceId));

        verify(carrierInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Carrier Invoice successfully")
    public void testUpdateCarrierInvoice_Success() {
        CarrierInvoiceDTO currentDTO = prepareCarrierInvoiceDTO();
        CarrierInvoiceDTO updatedDTO = prepareCarrierInvoiceDTO();
        CarrierInvoice carrierInvoice = prepareCarrierInvoice();

        when(carrierInvoiceMapper.mapToEntity(updatedDTO)).thenReturn(carrierInvoice);

        assertDoesNotThrow(() -> carrierInvoiceService.updateCarrierInvoice(currentDTO, updatedDTO));

        verify(carrierInvoiceRepository, times(1)).save(carrierInvoice);
    }

    @Test
    @DisplayName("Should throw CannotEditEntityException when trying to change isPaid value from true to false during update")
    public void testUpdateCarrierInvoice_CannotEditIsPaid() {
        CarrierInvoiceDTO currentDTO = preparePaidCarrierInvoiceDTO();
        CarrierInvoiceDTO updatedDTO = prepareCarrierInvoiceDTO();
        CarrierInvoice carrierInvoice = prepareCarrierInvoice();
        currentDTO.setIsPaid(true);
        updatedDTO.setIsPaid(false);

        when(carrierInvoiceMapper.mapToEntity(updatedDTO)).thenReturn(carrierInvoice);

        assertThrows(CannotEditEntityException.class, () -> carrierInvoiceService.updateCarrierInvoice(currentDTO, updatedDTO));
        assertTrue(currentDTO.getIsPaid());
        verify(carrierInvoiceRepository, never()).save(any());
    }

    private CarrierInvoiceDTO prepareCarrierInvoiceDTO() {
        CarrierInvoiceDTO invoiceDTO = new CarrierInvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber("12345");
        invoiceDTO.setOrderId(2L);
        invoiceDTO.setCarrierId(3L);
        invoiceDTO.setIsPaid(false);
        return invoiceDTO;
    }

    private CarrierInvoice prepareCarrierInvoice() {
        CarrierInvoice invoice = new CarrierInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("12345");
        invoice.setOrder(new ForwardingOrder());
        Carrier carrier = new Carrier();
        carrier.setId(1L);
        carrier.setBalance(BigDecimal.valueOf(1000));
        invoice.setCarrier(carrier);
        invoice.setIsPaid(false);
        invoice.setNetValue(BigDecimal.valueOf(1000));
        invoice.setGrossValue(BigDecimal.valueOf(1230));
        return invoice;
    }

    private CarrierInvoiceDTO preparePaidCarrierInvoiceDTO() {
        CarrierInvoiceDTO invoiceDTO = new CarrierInvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber("12345");
        invoiceDTO.setOrderId(2L);
        invoiceDTO.setCarrierId(3L);
        invoiceDTO.setIsPaid(true);
        return invoiceDTO;
    }

    private CarrierInvoice preparePaidCarrierInvoice() {
        CarrierInvoice invoice = new CarrierInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("12345");
        invoice.setOrder(new ForwardingOrder());
        invoice.setCarrier(new Carrier());
        invoice.setIsPaid(true);
        return invoice;
    }

    private ForwardingOrder prepareForwardingOrder() {
        ForwardingOrder order = new ForwardingOrder();
        order.setId(2L);
        order.setPrice(BigDecimal.valueOf(1000));
        return order;
    }

    private Carrier prepareCarrier() {
        Carrier carrier = new Carrier();
        carrier.setId(3L);
        carrier.setVatNumber("PL123456789");
        carrier.setTermOfPayment(14);
        return carrier;
    }

}