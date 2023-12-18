package com.tsl.controller;

import com.tsl.dtos.CustomerInvoiceDTO;
import com.tsl.service.CustomerInvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/invoices/customer")
public class CustomerInvoiceController {

    private final CustomerInvoiceService customerInvoiceService;

    public CustomerInvoiceController(CustomerInvoiceService customerInvoiceService) {
        this.customerInvoiceService = customerInvoiceService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerInvoiceDTO>> findAllCustomerInvoices(){
        List<CustomerInvoiceDTO> allCustomerInvoices = customerInvoiceService.findAllCustomerInvoices();
        if (allCustomerInvoices.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCustomerInvoices);
    }
    @PostMapping
    public ResponseEntity<CustomerInvoiceDTO> addInvoiceForCustomer(@RequestBody @Valid CustomerInvoiceDTO customerInvoiceDTO){
        CustomerInvoiceDTO created = customerInvoiceService.addCustomerInvoice(customerInvoiceDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
