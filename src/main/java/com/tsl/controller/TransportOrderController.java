package com.tsl.controller;

import com.tsl.dtos.ForwardingOrderDTO;
import com.tsl.dtos.TransportOrderDTO;
import com.tsl.service.TransportOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transport-orders")
public class TransportOrderController {
    private final TransportOrderService transportOrderService;

    public TransportOrderController(TransportOrderService transportOrderService) {
        this.transportOrderService = transportOrderService;
    }

    @GetMapping
    public ResponseEntity<List<TransportOrderDTO>> findAllTransportOrders(){
        List<TransportOrderDTO> allOrders = transportOrderService.findAllTransportOrders();
        if (allOrders.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allOrders);
    }

    @PostMapping
    public ResponseEntity<TransportOrderDTO> addTransportOrder(@RequestBody @Valid TransportOrderDTO transportOrderDTO){
        TransportOrderDTO created = transportOrderService.addTransportOrder(transportOrderDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
