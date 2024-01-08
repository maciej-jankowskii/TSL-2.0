package com.tsl.mapper;

import com.tsl.dtos.CustomerDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.ContactPersonNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.address.Address;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.model.contractor.Customer;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.ContactPersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerMapper {

    public Customer mapToEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            throw new NullEntityException("Customer data cannot be null");
        }
        Customer customer = new Customer();
        customer.setId(customerDTO.getId());
        customer.setFullName(customerDTO.getFullName());
        customer.setShortName(customerDTO.getShortName());
        customer.setVatNumber(customerDTO.getVatNumber());
        customer.setDescription(customerDTO.getDescription());
        customer.setTermOfPayment(customerDTO.getTermOfPayment());
        return customer;
    }

    public CustomerDTO mapToDTO(Customer customer) {
        if (customer == null) {
            throw new NullEntityException("Customer cannot be null");
        }
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setShortName(customer.getShortName());
        List<Long> contactPersons = customer.getContactPersons().stream().map(ContactPerson::getId).collect(Collectors.toList());
        dto.setContactPersonIds(contactPersons);
        dto.setVatNumber(customer.getVatNumber());
        dto.setAddressId(customer.getAddress().getId());
        dto.setDescription(customer.getDescription());
        dto.setTermOfPayment(customer.getTermOfPayment());
        return dto;
    }

}
