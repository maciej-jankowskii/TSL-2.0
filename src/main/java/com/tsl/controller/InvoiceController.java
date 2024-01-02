package com.tsl.controller;

import com.tsl.dtos.CarrierInvoiceDTO;
import com.tsl.dtos.CustomerInvoiceDTO;
import com.tsl.service.CarrierInvoiceService;
import com.tsl.service.CustomerInvoiceService;
import com.tsl.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final CarrierInvoiceService carrierInvoiceService;
    private final CustomerInvoiceService customerInvoiceService;

    public InvoiceController(CarrierInvoiceService carrierInvoiceService, CustomerInvoiceService customerInvoiceService) {
        this.carrierInvoiceService = carrierInvoiceService;
        this.customerInvoiceService = customerInvoiceService;
    }

    @GetMapping("/carrier")
    public ResponseEntity<List<CarrierInvoiceDTO>> findAllCarrierInvoices(){
        List<CarrierInvoiceDTO> allInvoices = carrierInvoiceService.findAll();
        if (allInvoices.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allInvoices);
    }

    @PostMapping("/carrier")
    public ResponseEntity<CarrierInvoiceDTO> addInvoiceFromCarrier(@RequestBody @Valid CarrierInvoiceDTO dto){
        CarrierInvoiceDTO created = carrierInvoiceService.addCarrierInvoice(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/carrier/{id}/paid")
    public ResponseEntity<?> markCarrierInvoiceAsPaid(@PathVariable Long id){
        carrierInvoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer")
    public ResponseEntity<List<CustomerInvoiceDTO>> findAllCustomerInvoices(){
        List<CustomerInvoiceDTO> allCustomerInvoices = customerInvoiceService.findAllCustomerInvoices();
        if (allCustomerInvoices.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCustomerInvoices);
    }
    @PostMapping("/customer")
    public ResponseEntity<CustomerInvoiceDTO> addInvoiceForCustomer(@RequestBody @Valid CustomerInvoiceDTO customerInvoiceDTO){
        CustomerInvoiceDTO created = customerInvoiceService.addCustomerInvoice(customerInvoiceDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/customer/{id}/paid")
    public ResponseEntity<?> markCustomerInvoiceAsPaid(@PathVariable Long id){
        customerInvoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.noContent().build();
    }
}
