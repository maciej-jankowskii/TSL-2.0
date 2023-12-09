package com.tsl.controller;

import com.tsl.dtos.WarehouseDTO;
import com.tsl.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
