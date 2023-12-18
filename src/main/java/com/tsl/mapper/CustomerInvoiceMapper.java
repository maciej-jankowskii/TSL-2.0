package com.tsl.mapper;

import com.tsl.dtos.CustomerInvoiceDTO;
import com.tsl.exceptions.CarrierNotFoundException;
import com.tsl.exceptions.CustomerNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.model.invoice.CustomerInvoice;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerInvoiceMapper {

    private final CargoRepository cargoRepository;
    private final CustomerRepository customerRepository;

    public CustomerInvoiceMapper(CargoRepository cargoRepository, CustomerRepository customerRepository) {
        this.cargoRepository = cargoRepository;
        this.customerRepository = customerRepository;
    }

    public CustomerInvoice mapToEntity(CustomerInvoiceDTO customerInvoiceDTO){
        if (customerInvoiceDTO == null){
            throw new NullEntityException("Customer invoice data cannot be null");
        }

        CustomerInvoice customerInvoice = new CustomerInvoice();
        customerInvoice.setId(customerInvoiceDTO.getId());
        customerInvoice.setInvoiceNumber(customerInvoiceDTO.getInvoiceNumber());
        customerInvoice.setInvoiceDate(customerInvoiceDTO.getInvoiceDate());
        customerInvoice.setDueDate(customerInvoiceDTO.getDueDate());
        customerInvoice.setNetValue(customerInvoiceDTO.getNetValue());
        customerInvoice.setGrossValue(customerInvoiceDTO.getGrossValue());
        customerInvoice.setIsPaid(customerInvoiceDTO.getIsPaid());

        Cargo cargo = cargoRepository.findById(customerInvoiceDTO.getCargoId()).orElseThrow(() -> new CarrierNotFoundException("Cargo not found"));
        customerInvoice.setCargo(cargo);

        Customer customer = customerRepository.findById(customerInvoiceDTO.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        customerInvoice.setCustomer(customer);

        return customerInvoice;
    }

    public CustomerInvoiceDTO mapToDTO(CustomerInvoice customerInvoice){
        if (customerInvoice == null){
            throw new NullEntityException("Customer invoice cannot be null");
        }

        CustomerInvoiceDTO dto = new CustomerInvoiceDTO();
        dto.setId(customerInvoice.getId());
        dto.setInvoiceNumber(customerInvoice.getInvoiceNumber());
        dto.setInvoiceDate(customerInvoice.getInvoiceDate());
        dto.setDueDate(customerInvoice.getDueDate());
        dto.setNetValue(customerInvoice.getNetValue());
        dto.setGrossValue(customerInvoice.getGrossValue());
        dto.setIsPaid(customerInvoice.getIsPaid());
        dto.setCargoId(customerInvoice.getCargo().getId());
        dto.setCustomerId(customerInvoice.getCustomer().getId());
        return dto;
    }
}
