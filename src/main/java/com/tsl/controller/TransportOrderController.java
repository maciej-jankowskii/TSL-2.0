package com.tsl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tsl.dtos.transport.TransportOrderDTO;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.repository.employees.TransportPlannerRepository;
import com.tsl.service.forwardingAndTransport.TransportOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transport-orders")
@RequiredArgsConstructor
public class TransportOrderController {
    private final TransportOrderService transportOrderService;
    private final TransportPlannerRepository transportPlannerRepository;
    private final ObjectMapper objectMapper;

    /***
     Handling requests related to reading, adding, updating transport orders
     */

    @GetMapping
    public ResponseEntity<List<TransportOrderDTO>> findAllTransportOrders() {
        List<TransportOrderDTO> allOrders = transportOrderService.findAllTransportOrders();
        if (allOrders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allOrders);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<TransportOrderDTO>> findAllTransportOrdersSortedBy(@RequestParam String sortBy) {
        TransportPlanner planner = getLoggedInUser();
        List<TransportOrderDTO> sortedOrders = transportOrderService.findAllTransportOrdersSortedBy(planner, sortBy);
        return ResponseEntity.ok(sortedOrders);
    }

    @PostMapping
    public ResponseEntity<TransportOrderDTO> addTransportOrder(@RequestBody @Valid TransportOrderDTO transportOrderDTO) {
        TransportOrderDTO created = transportOrderService.addTransportOrder(transportOrderDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTransportOrder(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        TransportOrderDTO orderDTO = transportOrderService.findTransportOrderById(id);
        applyPatchAndUpdateOrder(orderDTO, patch);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransportOrder(@PathVariable Long id) {
        transportOrderService.cancelTransportOrder(id);
        return ResponseEntity.noContent().build();
    }

    /***
     Helper methods
     */

    private void applyPatchAndUpdateOrder(TransportOrderDTO orderDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode orderNode = objectMapper.valueToTree(orderDTO);
        JsonNode patchedOrder = patch.apply(orderNode);
        TransportOrderDTO patchedOrderDTO = objectMapper.treeToValue(patchedOrder, TransportOrderDTO.class);
        transportOrderService.updateTransportOrder(orderDTO, patchedOrderDTO);
    }

    private TransportPlanner getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return transportPlannerRepository.findByEmail(userEmail).orElseThrow(() -> new EmployeeNotFoundException("Transport planner not found"));
    }
}
