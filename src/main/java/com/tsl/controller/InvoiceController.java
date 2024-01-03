package com.tsl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tsl.dtos.CarrierInvoiceDTO;
import com.tsl.dtos.CustomerInvoiceDTO;
import com.tsl.dtos.WarehouseOrderInvoiceDTO;
import com.tsl.service.CarrierInvoiceService;
import com.tsl.service.CustomerInvoiceService;
import com.tsl.service.WarehouseOrderInvoiceService;
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
    private final ObjectMapper objectMapper;
    private final WarehouseOrderInvoiceService warehouseOrderInvoiceService;

    public InvoiceController(CarrierInvoiceService carrierInvoiceService, CustomerInvoiceService customerInvoiceService, ObjectMapper objectMapper, WarehouseOrderInvoiceService warehouseOrderInvoiceService) {
        this.carrierInvoiceService = carrierInvoiceService;
        this.customerInvoiceService = customerInvoiceService;
        this.objectMapper = objectMapper;
        this.warehouseOrderInvoiceService = warehouseOrderInvoiceService;
    }

    /***
     Handling requests related to Carrier invoices
     */

    @GetMapping("/carrier")
    public ResponseEntity<List<CarrierInvoiceDTO>> findAllCarrierInvoices() {
        List<CarrierInvoiceDTO> allInvoices = carrierInvoiceService.findAll();
        if (allInvoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allInvoices);
    }

    @PostMapping("/carrier")
    public ResponseEntity<CarrierInvoiceDTO> addInvoiceFromCarrier(@RequestBody @Valid CarrierInvoiceDTO dto) {
        CarrierInvoiceDTO created = carrierInvoiceService.addCarrierInvoice(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/carrier/{id}/paid")
    public ResponseEntity<?> markCarrierInvoiceAsPaid(@PathVariable Long id) {
        carrierInvoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/carrier/sorted")
    public ResponseEntity<List<CarrierInvoiceDTO>> findAllCarrierInvoicesSortedBy(@RequestParam String sortBy) {
        List<CarrierInvoiceDTO> sortedCarrierInvoices = carrierInvoiceService.findAllCarrierInvoicesSortedBy(sortBy);
        return ResponseEntity.ok(sortedCarrierInvoices);
    }

    @PatchMapping("/carrier/{id}")
    public ResponseEntity<?> updateCarrierInvoice(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        CarrierInvoiceDTO invoiceDTO = carrierInvoiceService.findCarrierInvoiceById(id);
        applyPatchAndUpdateCarrierInvoice(invoiceDTO, patch);
        return ResponseEntity.noContent().build();

    }

    private void applyPatchAndUpdateCarrierInvoice(CarrierInvoiceDTO invoiceDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode invoiceNode = objectMapper.valueToTree(invoiceDTO);
        JsonNode patchedInvoice = patch.apply(invoiceNode);
        CarrierInvoiceDTO patchedInvoiceDTO = objectMapper.treeToValue(patchedInvoice, CarrierInvoiceDTO.class);
        carrierInvoiceService.updateCarrierInvoice(invoiceDTO, patchedInvoiceDTO);
    }

    /***
     Handling requests related to Customer invoices
     */

    @GetMapping("/customer")
    public ResponseEntity<List<CustomerInvoiceDTO>> findAllCustomerInvoices() {
        List<CustomerInvoiceDTO> allCustomerInvoices = customerInvoiceService.findAllCustomerInvoices();
        if (allCustomerInvoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCustomerInvoices);
    }

    @PostMapping("/customer")
    public ResponseEntity<CustomerInvoiceDTO> addInvoiceForCustomer(@RequestBody @Valid CustomerInvoiceDTO customerInvoiceDTO) {
        CustomerInvoiceDTO created = customerInvoiceService.addCustomerInvoice(customerInvoiceDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/customer/{id}/paid")
    public ResponseEntity<?> markCustomerInvoiceAsPaid(@PathVariable Long id) {
        customerInvoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/sorted")
    public ResponseEntity<List<CustomerInvoiceDTO>> findAllCustomerInvoicesSortedBy(@RequestParam String sortBy) {
        List<CustomerInvoiceDTO> sortedCustomerInvoices = customerInvoiceService.findAllCustomerInvoicesSortedBy(sortBy);
        return ResponseEntity.ok(sortedCustomerInvoices);
    }

    @PatchMapping("/customer/{id}")
    public ResponseEntity<?> updateCustomerInvoice(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        CustomerInvoiceDTO invoiceDTO = customerInvoiceService.findCustomerInvoiceById(id);
        applyPatchAndUpdateCustomerInvoice(invoiceDTO, patch);
        return ResponseEntity.noContent().build();

    }

    private void applyPatchAndUpdateCustomerInvoice(CustomerInvoiceDTO invoiceDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode invoiceNode = objectMapper.valueToTree(invoiceDTO);
        JsonNode patchedInvoice = patch.apply(invoiceNode);
        CustomerInvoiceDTO patchedInvoiceDTO = objectMapper.treeToValue(patchedInvoice, CustomerInvoiceDTO.class);
        customerInvoiceService.updateCustomerInvoice(invoiceDTO, patchedInvoiceDTO);
    }

    /***
     Handling requests related to Warehouse order invoices
     */

    @GetMapping("/warehouse-order")
    public ResponseEntity<List<WarehouseOrderInvoiceDTO>> findAllWarehouseInvoices() {
        List<WarehouseOrderInvoiceDTO> allInvoices = warehouseOrderInvoiceService.findAllWarehouseInvoices();
        if (allInvoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allInvoices);
    }

    @PostMapping("/warehouse-order")
    public ResponseEntity<WarehouseOrderInvoiceDTO> addWarehouseInvoice(@RequestBody @Valid WarehouseOrderInvoiceDTO dto) {
        WarehouseOrderInvoiceDTO created = warehouseOrderInvoiceService.addWarehouseInvoice(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/warehouse-order/{id}/paid")
    public ResponseEntity<?> markWarehouseInvoiceAsPaid(@PathVariable Long id) {
        warehouseOrderInvoiceService.markInvoiceAsPaid(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse-order/sorted")
    public ResponseEntity<List<WarehouseOrderInvoiceDTO>> findAllWarehouseInvoicesSortedBy(@RequestParam String sortBy) {
        List<WarehouseOrderInvoiceDTO> sortedWarehouseInvoices = warehouseOrderInvoiceService.findAllWarehouseInvoicesSortedBy(sortBy);
        return ResponseEntity.ok(sortedWarehouseInvoices);
    }

    @PatchMapping("/warehouse-order/{id}")
    public ResponseEntity<?> updateWarehouseInvoice(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        WarehouseOrderInvoiceDTO invoiceDTO = warehouseOrderInvoiceService.findWarehouseInvoiceById(id);
        applyPatchAndUpdateWarehouseInvoice(invoiceDTO, patch);
        return ResponseEntity.noContent().build();
    }

    private void applyPatchAndUpdateWarehouseInvoice(WarehouseOrderInvoiceDTO invoiceDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode invoiceNode = objectMapper.valueToTree(invoiceDTO);
        JsonNode patchedInvoice = patch.apply(invoiceNode);
        WarehouseOrderInvoiceDTO patchedInvoiceDTO = objectMapper.treeToValue(patchedInvoice, WarehouseOrderInvoiceDTO.class);
        warehouseOrderInvoiceService.updateWarehouseInvoice(invoiceDTO, patchedInvoiceDTO);
    }
}
