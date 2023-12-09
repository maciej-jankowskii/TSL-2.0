package com.tsl.controller;

import com.tsl.dtos.WarehouseDTO;
import com.tsl.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public ResponseEntity<List<WarehouseDTO>> findAllWarehouse(){
        List<WarehouseDTO> allWarehouses = warehouseService.findAllWarehouses();
        if (allWarehouses.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allWarehouses);
    }

    @PostMapping
    public ResponseEntity<WarehouseDTO> addWarehouse(@RequestBody WarehouseDTO warehouseDTO){
        WarehouseDTO created = warehouseService.addWarehouse(warehouseDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
