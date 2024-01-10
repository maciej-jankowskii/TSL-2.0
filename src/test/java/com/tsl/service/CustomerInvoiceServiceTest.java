package com.tsl.service;

import com.tsl.dtos.CustomerInvoiceDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.CustomerInvoiceMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.model.invoice.CustomerInvoice;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.CustomerInvoiceRepository;
import com.tsl.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerInvoiceServiceTest {

    @Mock private CargoRepository cargoRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerInvoiceRepository customerInvoiceRepository;
    @Mock private CustomerInvoiceMapper customerInvoiceMapper;
    @Mock private VatCalculatorService vatCalculatorService;
    @InjectMocks private CustomerInvoiceService customerInvoiceService;
    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Customer Invoices successfully")
    public void testFindAllCustomerInvoices_Success() {
        CustomerInvoice invoice1 = prepareCustomerInvoice();
        CustomerInvoice invoice2 = prepareCustomerInvoice();
        CustomerInvoiceDTO invoiceDTO1 = prepareCustomerInvoiceDTO();
        CustomerInvoiceDTO invoiceDTO2 = prepareCustomerInvoiceDTO();

        when(customerInvoiceRepository.findAll()).thenReturn(Arrays.asList(invoice1, invoice2));
        when(customerInvoiceMapper.mapToDTO(invoice1)).thenReturn(invoiceDTO1);
        when(customerInvoiceMapper.mapToDTO(invoice2)).thenReturn(invoiceDTO2);

        List<CustomerInvoiceDTO> resultInvoices = customerInvoiceService.findAllCustomerInvoices();

        assertNotNull(resultInvoices);
        assertEquals(2, resultInvoices.size());

        verify(customerInvoiceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Customer Invoice by ID successfully")
    public void testFindCustomerInvoiceById_Success() {
        Long invoiceId = 1L;
        CustomerInvoice invoice = prepareCustomerInvoice();
        CustomerInvoiceDTO invoiceDTO = prepareCustomerInvoiceDTO();

        when(customerInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(customerInvoiceMapper.mapToDTO(invoice)).thenReturn(invoiceDTO);

        CustomerInvoiceDTO resultInvoice = customerInvoiceService.findCustomerInvoiceById(invoiceId);

        assertNotNull(resultInvoice);
        assertEquals("12345", resultInvoice.getInvoiceNumber());

        verify(customerInvoiceRepository, times(1)).findById(invoiceId);
    }

    @Test
    @DisplayName("Should throw InvoiceNotFoundException when Customer Invoice is not found by ID")
    public void testFindCustomerInvoiceById_InvoiceNotFound() {
        Long invoiceId = 1L;

        when(customerInvoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> customerInvoiceService.findCustomerInvoiceById(invoiceId));

        verify(customerInvoiceMapper, never()).mapToDTO(any());
    }

    @Test
    @DisplayName("Should find all overdue Customer Invoices successfully")
    public void testFindAllOverdueInvoices_Success() {
        LocalDate currentDate = LocalDate.now();
        CustomerInvoice invoice1 = prepareCustomerInvoice();
        CustomerInvoice invoice2 = prepareCustomerInvoice();
        CustomerInvoice invoice3 = prepareCustomerInvoice();
        invoice1.setDueDate(currentDate.minusDays(2));
        invoice2.setDueDate(currentDate.minusDays(2));
        invoice3.setDueDate(currentDate.plusDays(2));

        CustomerInvoiceDTO invoiceDTO1 = prepareCustomerInvoiceDTO();
        CustomerInvoiceDTO invoiceDTO2 = prepareCustomerInvoiceDTO();
        CustomerInvoiceDTO invoiceDTO3 = prepareCustomerInvoiceDTO();

        when(customerInvoiceRepository.findAll()).thenReturn(Arrays.asList(invoice1, invoice2, invoice3));
        when(customerInvoiceMapper.mapToDTO(invoice1)).thenReturn(invoiceDTO1);
        when(customerInvoiceMapper.mapToDTO(invoice2)).thenReturn(invoiceDTO2);
        when(customerInvoiceMapper.mapToDTO(invoice3)).thenReturn(invoiceDTO3);

        List<CustomerInvoiceDTO> overdueInvoices = customerInvoiceService.findAllOverdueInvoices();

        assertNotNull(overdueInvoices);
        assertEquals(2, overdueInvoices.size());

        verify(customerInvoiceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should add Customer Invoice successfully")
    public void testAddCustomerInvoice_Success() {
        CustomerInvoiceDTO invoiceDTO = prepareCustomerInvoiceDTO();
        CustomerInvoice invoice = prepareCustomerInvoice();
        Cargo cargo = prepareCargo();
        Customer customer = prepareCustomer();

        when(customerInvoiceMapper.mapToEntity(invoiceDTO)).thenReturn(invoice);
        when(cargoRepository.findById(invoiceDTO.getCargoId())).thenReturn(Optional.of(cargo));
        when(customerRepository.findById(invoiceDTO.getCustomerId())).thenReturn(Optional.of(customer));
        when(vatCalculatorService.calculateGrossValue(cargo.getPrice(), customer.getVatNumber())).thenReturn(BigDecimal.TEN);
        when(customerInvoiceRepository.save(invoice)).thenReturn(invoice);
        when(customerInvoiceMapper.mapToDTO(invoice)).thenReturn(invoiceDTO);

        CustomerInvoiceDTO result = customerInvoiceService.addCustomerInvoice(invoiceDTO);

        assertNotNull(result);
        assertEquals("12345", result.getInvoiceNumber());

        verify(customerInvoiceRepository, times(1)).save(invoice);
    }

    @Test
    @DisplayName("Should throw CargoNotFoundException when Cargo is not found during adding Customer Invoice")
    public void testAddCustomerInvoice_CargoNotFound() {
        CustomerInvoiceDTO invoiceDTO = prepareCustomerInvoiceDTO();

        when(customerInvoiceMapper.mapToEntity(invoiceDTO)).thenReturn(new CustomerInvoice());
        when(cargoRepository.findById(invoiceDTO.getCargoId())).thenReturn(Optional.empty());

        assertThrows(CargoNotFoundException.class, () -> customerInvoiceService.addCustomerInvoice(invoiceDTO));


        verify(customerRepository, never()).findById(any());
        verify(vatCalculatorService, never()).calculateGrossValue(any(), any());
        verify(customerInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when Customer is not found during adding Customer Invoice")
    public void testAddCustomerInvoice_CustomerNotFound() {
        CustomerInvoiceDTO invoiceDTO = prepareCustomerInvoiceDTO();
        CustomerInvoice invoice = prepareCustomerInvoice();
        Cargo cargo = prepareCargo();
        when(customerInvoiceMapper.mapToEntity(invoiceDTO)).thenReturn(invoice);
        when(cargoRepository.findById(invoiceDTO.getCargoId())).thenReturn(Optional.of(cargo));
        when(customerRepository.findById(invoiceDTO.getCustomerId())).thenReturn(Optional.empty());


        assertThrows(CustomerNotFoundException.class, () -> customerInvoiceService.addCustomerInvoice(invoiceDTO));

        verify(customerInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should mark Customer Invoice as Paid successfully")
    public void testMarkInvoiceAsPaid_Success() {
        Long invoiceId = 1L;
        CustomerInvoice invoice = prepareCustomerInvoice();
        CustomerInvoiceDTO dto = preparePaidCustomerInvoiceDTO();
        invoice.setIsPaid(false);


        when(customerInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(customerInvoiceRepository.save(invoice)).thenReturn(invoice);
        when(customerInvoiceMapper.mapToDTO(invoice)).thenReturn(dto);

        CustomerInvoiceDTO result = customerInvoiceService.markInvoiceAsPaid(invoiceId);

        assertNotNull(result);
        assertTrue(result.getIsPaid());

        verify(customerInvoiceRepository, times(1)).save(invoice);
    }

    @Test
    @DisplayName("Should throw InvoiceNotFoundException when Customer Invoice is not found during marking as Paid")
    public void testMarkInvoiceAsPaid_InvoiceNotFound() {
        Long invoiceId = 1L;

        when(customerInvoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> customerInvoiceService.markInvoiceAsPaid(invoiceId));

        verify(customerInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvoiceAlreadyPaidException when Customer Invoice is already paid during marking as Paid")
    public void testMarkInvoiceAsPaid_AlreadyPaid() {
        Long invoiceId = 1L;
        CustomerInvoice invoice = preparePaidCustomerInvoice();

        when(customerInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThrows(InvoiceAlreadyPaidException.class, () -> customerInvoiceService.markInvoiceAsPaid(invoiceId));

        verify(customerInvoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Customer Invoice successfully")
    public void testUpdateCustomerInvoice_Success() {
        CustomerInvoiceDTO currentDTO = prepareCustomerInvoiceDTO();
        CustomerInvoiceDTO updatedDTO = prepareCustomerInvoiceDTO();
        CustomerInvoice customerInvoice = prepareCustomerInvoice();

        when(customerInvoiceMapper.mapToEntity(updatedDTO)).thenReturn(customerInvoice);

        assertDoesNotThrow(() -> customerInvoiceService.updateCustomerInvoice(currentDTO, updatedDTO));

        verify(customerInvoiceRepository, times(1)).save(customerInvoice);
    }

    @Test
    @DisplayName("Should throw CannotEditEntityException when trying to change isPaid value from true to false during update")
    public void testUpdateCustomerInvoice_CannotEditIsPaid() {
        CustomerInvoiceDTO currentDTO = preparePaidCustomerInvoiceDTO();
        CustomerInvoiceDTO updatedDTO = prepareCustomerInvoiceDTO();
        CustomerInvoice invoice = prepareCustomerInvoice();
        currentDTO.setIsPaid(true);
        updatedDTO.setIsPaid(false);

        when(customerInvoiceMapper.mapToEntity(updatedDTO)).thenReturn(invoice);
        assertThrows(CannotEditEntityException.class, () -> customerInvoiceService.updateCustomerInvoice(currentDTO, updatedDTO));

        verify(customerInvoiceRepository, never()).save(any());
    }


    private CustomerInvoiceDTO prepareCustomerInvoiceDTO() {
        CustomerInvoiceDTO invoiceDTO = new CustomerInvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber("12345");
        invoiceDTO.setCargoId(2L);
        invoiceDTO.setCustomerId(3L);
        invoiceDTO.setIsPaid(false);
        return invoiceDTO;
    }

    private CustomerInvoiceDTO preparePaidCustomerInvoiceDTO() {
        CustomerInvoiceDTO invoiceDTO = new CustomerInvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceNumber("12345");
        invoiceDTO.setCargoId(2L);
        invoiceDTO.setCustomerId(3L);
        invoiceDTO.setIsPaid(true);
        return invoiceDTO;
    }

    private CustomerInvoice prepareCustomerInvoice() {
        CustomerInvoice invoice = new CustomerInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("12345");
        invoice.setNetValue(BigDecimal.valueOf(1000));
        invoice.setGrossValue(BigDecimal.valueOf(1230));
        Cargo cargo = new Cargo();
        cargo.setId(1L);
        cargo.setPrice(BigDecimal.valueOf(1000));

        invoice.setCargo(cargo);
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setBalance(BigDecimal.valueOf(10000));
        invoice.setCustomer(customer);
        invoice.setIsPaid(false);
        return invoice;
    }

    private CustomerInvoice preparePaidCustomerInvoice() {
        CustomerInvoice invoice = new CustomerInvoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("12345");
        Cargo cargo = new Cargo();
        cargo.setId(1L);
        cargo.setPrice(BigDecimal.valueOf(1000));

        invoice.setCargo(cargo);
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setBalance(BigDecimal.valueOf(10000));
        invoice.setCustomer(customer);
        invoice.setIsPaid(true);
        return invoice;
    }

    private Cargo prepareCargo() {
        Cargo cargo = new Cargo();
        cargo.setId(2L);
        cargo.setPrice(BigDecimal.valueOf(1000));
        return cargo;
    }

    private Customer prepareCustomer() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setVatNumber("PL123456789");
        customer.setTermOfPayment(14);
        return customer;
    }

}