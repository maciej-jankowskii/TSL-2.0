package com.tsl.controller;

import com.tsl.dtos.CustomerDTO;
import com.tsl.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> findAllCustomers(){
        List<CustomerDTO> allCustomers = customerService.findAllCustomers();
        if (allCustomers.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCustomers);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> addCustomer(@RequestBody @Valid CustomerDTO customerDTO){
        CustomerDTO created = customerService.addCustomer(customerDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
