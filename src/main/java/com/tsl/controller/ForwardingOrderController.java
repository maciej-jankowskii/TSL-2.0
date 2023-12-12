package com.tsl.controller;

import com.tsl.dtos.ForwardingOrderDTO;
import com.tsl.service.ForwarderOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/forwarding-orders")
public class ForwardingOrderController {

    private final ForwarderOrderService forwarderOrderService;

    public ForwardingOrderController(ForwarderOrderService forwarderOrderService) {
        this.forwarderOrderService = forwarderOrderService;
    }

    @GetMapping
    public ResponseEntity<List<ForwardingOrderDTO>> findAllForwardingOrders(){
        List<ForwardingOrderDTO> allForwardingOrders = forwarderOrderService.findAllForwardingOrders();
        if (allForwardingOrders.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(allForwardingOrders);
    }

    @PostMapping
    public ResponseEntity<ForwardingOrderDTO> addForwardingOrder(@RequestBody @Valid ForwardingOrderDTO forwardingOrderDTO){
        ForwardingOrderDTO created = forwarderOrderService.addForwardingOrder(forwardingOrderDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
