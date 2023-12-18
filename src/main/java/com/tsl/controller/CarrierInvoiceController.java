package com.tsl.controller;

import com.tsl.dtos.CarrierInvoiceDTO;
import com.tsl.service.CarrierInvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/invoices/carrier")
public class CarrierInvoiceController {

    private final CarrierInvoiceService carrierInvoiceService;

    public CarrierInvoiceController(CarrierInvoiceService carrierInvoiceService) {
        this.carrierInvoiceService = carrierInvoiceService;
    }

    @GetMapping
    public ResponseEntity<List<CarrierInvoiceDTO>> findAllCarrierInvoices(){
        List<CarrierInvoiceDTO> allInvoices = carrierInvoiceService.findAll();
        if (allInvoices.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allInvoices);
    }

    @PostMapping
    public ResponseEntity<CarrierInvoiceDTO> addInvoiceFromCarrier(@RequestBody @Valid CarrierInvoiceDTO dto){
        CarrierInvoiceDTO created = carrierInvoiceService.addCarrierInvoice(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/{id}/paid")
    public ResponseEntity<?> markInvoiceAsPaid(@PathVariable Long id){
        carrierInvoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.noContent().build();
    }

}
