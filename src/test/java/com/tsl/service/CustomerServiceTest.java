package com.tsl.service;

import com.tsl.dtos.forwardiing.CustomerDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.CustomerNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.mapper.CustomerMapper;
import com.tsl.model.address.Address;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.model.contractor.Customer;
import com.tsl.repository.contactAndAddress.AddressRepository;
import com.tsl.repository.contactAndAddress.ContactPersonRepository;
import com.tsl.repository.forwardingAndTransport.CustomerRepository;
import com.tsl.service.forwardingAndTransport.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ContactPersonRepository contactPersonRepository;
    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Customers")
    public void testFindAllCustomers_Success() {
        Customer customer1 = prepareFirstCustomer();
        Customer customer2 = prepareSecondCustomer();
        CustomerDTO customerDTO1 = prepareFirstDTO();
        CustomerDTO customerDTO2 = prepareSecondDTO();
        List<Customer> customers = Arrays.asList(customer1, customer2);

        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.mapToDTO(customer1)).thenReturn(customerDTO1);
        when(customerMapper.mapToDTO(customer2)).thenReturn(customerDTO2);
        List<CustomerDTO> resultCustomers = customerService.findAllCustomers();

        assertNotNull(resultCustomers);
        assertEquals(resultCustomers.size(), customers.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Customer by ID")
    public void testFindCustomerById_Success() {
        Customer customer = prepareFirstCustomer();
        CustomerDTO customerDTO = prepareFirstDTO();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerMapper.mapToDTO(customer)).thenReturn(customerDTO);

        CustomerDTO resultCustomer = customerService.findCustomerById(customer.getId());

        assertEquals(resultCustomer, customerDTO);
        verify(customerRepository, times(1)).findById(customer.getId());

    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException")
    public void testFindCustomerById_CustomerNotFound() {
        Long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findCustomerById(customerId));
    }

    @Test
    @DisplayName("Should add new Customer")
    public void testAddCustomer_Success() {
        Customer customer = prepareFirstCustomer();
        CustomerDTO customerDTO = prepareFirstDTO();


        when(customerMapper.mapToEntity(customerDTO)).thenReturn(customer);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(new Address()));
        when(contactPersonRepository.findById(1L)).thenReturn(Optional.of(new ContactPerson()));
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.mapToDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.addCustomer(customerDTO);

        verify(customerRepository, times(1)).save(any());
        assertEquals("ABC SPED", result.getFullName());
    }

    @Test
    @DisplayName("Should throw NullEntityException when adding Customer with null input")
    public void testAddCustomer_NullInput() {
        CustomerDTO nullCustomer = null;
        when(customerMapper.mapToEntity(nullCustomer)).thenThrow(new NullEntityException("Customer data cannot be null"));

        assertThrows(NullEntityException.class, () -> customerService.addCustomer(nullCustomer));

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AddressNotFoundException when adding Customer with non-existent address ID")
    public void testAddCustomer_NonExistentAddress() {
        CustomerDTO customerDTO = prepareFirstDTO();

        when(addressRepository.findById(customerDTO.getAddressId())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> customerService.addCustomer(customerDTO));

        verify(customerRepository, never()).save(any());
    }


    private CustomerDTO prepareFirstDTO() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setFullName("ABC SPED");
        customerDTO.setAddressId(1L);
        customerDTO.setContactPersonIds(emptyList());
        return customerDTO;
    }

    private CustomerDTO prepareSecondDTO() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(2L);
        customerDTO.setFullName("XYZ SPED");
        return customerDTO;
    }

    private static Customer prepareSecondCustomer() {
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setFullName("XYZ SPED");
        return customer;
    }

    private static Customer prepareFirstCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFullName("ABC SPED");
        return customer;
    }
}