package com.tsl.service;

import com.tsl.dtos.CustomerDTO;
import com.tsl.enums.PaymentRating;
import com.tsl.exceptions.CustomerNotFoundException;
import com.tsl.mapper.CustomerMapper;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.model.contractor.Customer;
import com.tsl.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    /**
     * Finding methods
     */

    public List<CustomerDTO> findAllCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<CustomerDTO> findAllCustomersSortedBy(String sortBy) {
        return customerRepository.findAllCustomersBy(sortBy).stream().map(customerMapper::mapToDTO).collect(Collectors.toList());
    }

    public CustomerDTO findCustomerById(Long id) {
        return customerRepository.findById(id).map(customerMapper::mapToDTO).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }

    /**
     * Create, update methods
     */

    @Transactional
    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.mapToEntity(customerDTO);

        addAdditionalDataForCustomer(customer);
        addAdditionalDataForContactPerson(customer);

        Customer saved = customerRepository.save(customer);
        return customerMapper.mapToDTO(saved);
    }

    @Transactional
    public void updateCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.mapToEntity(customerDTO);

        customerRepository.save(customer);
    }

    /**
     * Helper methods
     */

    private static void addAdditionalDataForContactPerson(Customer customer) {
        List<ContactPerson> contactPersons = customer.getContactPersons();
        if (!contactPersons.isEmpty()) {
            contactPersons.forEach(person -> person.setContractor(customer));
        }
    }

    private static void addAdditionalDataForCustomer(Customer customer) {
        customer.setBalance(BigDecimal.ZERO);
        customer.setPaymentRating(PaymentRating.NONE);
    }
}
