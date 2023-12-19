package com.tsl.controller;

import com.tsl.dtos.GoodsDTO;
import com.tsl.dtos.WarehouseDTO;
import com.tsl.dtos.WarehouseOrderDTO;
import com.tsl.service.GoodsService;
import com.tsl.service.WarehouseOrderService;
import com.tsl.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final GoodsService goodsService;
    private final WarehouseOrderService warehouseOrderService;

    public WarehouseController(WarehouseService warehouseService, GoodsService goodsService, WarehouseOrderService warehouseOrderService) {
        this.warehouseService = warehouseService;
        this.goodsService = goodsService;
        this.warehouseOrderService = warehouseOrderService;
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
    public ResponseEntity<WarehouseDTO> addWarehouse(@RequestBody @Valid WarehouseDTO warehouseDTO){
        WarehouseDTO created = warehouseService.addWarehouse(warehouseDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/goods")
    public ResponseEntity<List<GoodsDTO>> findAllGoods(){
        List<GoodsDTO> allGoods = goodsService.findAll();
        if (allGoods.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allGoods);
    }

    @GetMapping("/goods/not-assigned")
    public ResponseEntity<List<GoodsDTO>> findAllNotAssignedGoods(){
        List<GoodsDTO> allNotAssignedToOrderGoods = goodsService.findAllNotAssignedToOrderGoods();
        if (allNotAssignedToOrderGoods.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allNotAssignedToOrderGoods);
    }

    @PostMapping("/goods")
    public ResponseEntity<GoodsDTO> addGoods(@RequestBody @Valid GoodsDTO goodsDTO){
        GoodsDTO created = goodsService.addGoods(goodsDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<WarehouseOrderDTO>> findAllWarehouseOrders(){
        List<WarehouseOrderDTO> allWarehouseOrders = warehouseOrderService.findAllWarehouseOrders();
        if (allWarehouseOrders.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allWarehouseOrders);
    }

    @PostMapping("/orders")
    public ResponseEntity<WarehouseOrderDTO> addWarehouseOrder(@RequestBody @Valid WarehouseOrderDTO warehouseOrderDTO){
        WarehouseOrderDTO created = warehouseOrderService.addWarehouseOrder(warehouseOrderDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("orders/not-completed")
    public ResponseEntity<List<WarehouseOrderDTO>> findNotCompletedWarehouseOrders(){
        List<WarehouseOrderDTO> allNotCompleted = warehouseOrderService.findAllNotCompletedWarehouseOrders();
        if (allNotCompleted.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allNotCompleted);
    }

    @PatchMapping("orders/complete/{id}")
    public ResponseEntity<?> markWarehouseOrderAsCompleted(@PathVariable Long id){
        warehouseOrderService.markWarehouseOrderAsCompleted(id);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/sorted")
    public ResponseEntity<List<WarehouseDTO>> findAllWarehousesSortedBy(@RequestParam String sortBy){
        List<WarehouseDTO> sortedWarehouses = warehouseService.findAllWarehousesSortedBy(sortBy);
        return ResponseEntity.ok(sortedWarehouses);
    }

    @GetMapping("/goods/sorted")
    public ResponseEntity<List<GoodsDTO>> findAllGoodsSortedBy(@RequestParam String sortBy){
        List<GoodsDTO> sortedGoods = goodsService.findAllGoodsSortedBY(sortBy);
        return ResponseEntity.ok(sortedGoods);
    }

    @GetMapping("/orders/sorted")
    public ResponseEntity<List<WarehouseOrderDTO>> findAllWarehouseOrdersSortedBy(@RequestParam String sortBy){
        List<WarehouseOrderDTO> sortedOrders = warehouseOrderService.findAllWarehouseOrdersSortedBy(sortBy);
        return ResponseEntity.ok(sortedOrders);
    }
}
