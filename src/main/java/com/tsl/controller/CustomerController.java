package com.tsl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tsl.dtos.CargoDTO;
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
    private final ObjectMapper objectMapper;
    public CustomerController(CustomerService customerService, ObjectMapper objectMapper) {
        this.customerService = customerService;
        this.objectMapper = objectMapper;
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

    @GetMapping("/sorted")
    public ResponseEntity<List<CustomerDTO>> findAllCustomersSortedBy(@RequestParam String sortBy) {
        List<CustomerDTO> sortedCustomers = customerService.findAllCustomersSortedBy(sortBy);
        return ResponseEntity.ok(sortedCustomers);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        CustomerDTO customerDTO = customerService.findCustomerById(id);
        applyPatchAndUpdateCustomer(customerDTO, patch);
        return ResponseEntity.noContent().build();

    }

    private void applyPatchAndUpdateCustomer(CustomerDTO customerDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode customerNode = objectMapper.valueToTree(customerDTO);
        JsonNode patchedCustomer = patch.apply(customerNode);
        CustomerDTO patchedCustomerDTO = objectMapper.treeToValue(patchedCustomer, CustomerDTO.class);
        customerService.updateCustomer(customerDTO, patchedCustomerDTO);
    }
}
