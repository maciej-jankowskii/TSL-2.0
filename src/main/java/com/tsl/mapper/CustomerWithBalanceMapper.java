package com.tsl.mapper;

import com.tsl.dtos.forwardiing.CustomerWithBalanceDTO;
import com.tsl.model.contractor.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerWithBalanceMapper {

    public CustomerWithBalanceDTO mapToDTO(Customer customer){
        CustomerWithBalanceDTO dto = new CustomerWithBalanceDTO();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setVatNumber(customer.getVatNumber());
        dto.setBalance(customer.getBalance());
        return dto;
    }
}
