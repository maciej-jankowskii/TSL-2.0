package com.tsl.controller;

import com.tsl.dtos.addressAndContact.AddressDTO;
import com.tsl.service.contactAndAddress.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;


    /***
     Handling requests related to reading and creating address
     */

    @GetMapping
    public ResponseEntity<List<AddressDTO>> findAllAddresses() {
        List<AddressDTO> allAddresses = addressService.findAllAddresses();
        if (allAddresses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allAddresses);
    }

    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(@RequestBody @Valid AddressDTO addressDTO) {
        AddressDTO created = addressService.createAddress(addressDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
