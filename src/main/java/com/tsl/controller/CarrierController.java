package com.tsl.controller;

import com.tsl.dtos.CarrierDTO;
import com.tsl.service.CarrierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/carriers")
public class CarrierController {

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @GetMapping
    public ResponseEntity<List<CarrierDTO>> findAllCarriers(){
        List<CarrierDTO> allCarriers = carrierService.findAllCarriers();
        if (allCarriers.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCarriers);
    }

    @PostMapping
    public ResponseEntity<CarrierDTO> addCarrier(@RequestBody @Valid CarrierDTO carrierDTO){
        CarrierDTO created = carrierService.addCarrier(carrierDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
