package com.tsl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tsl.dtos.ForwardingOrderDTO;
import com.tsl.exceptions.ForwarderNotFoundException;
import com.tsl.model.employee.Forwarder;
import com.tsl.repository.ForwarderRepository;
import com.tsl.service.ForwarderOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/forwarding-orders")
public class ForwardingOrderController {

    private final ForwarderOrderService forwarderOrderService;
    private final ForwarderRepository forwarderRepository;
    private final ObjectMapper objectMapper;

    public ForwardingOrderController(ForwarderOrderService forwarderOrderService, ForwarderRepository forwarderRepository, ObjectMapper objectMapper) {
        this.forwarderOrderService = forwarderOrderService;
        this.forwarderRepository = forwarderRepository;
        this.objectMapper = objectMapper;
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

    @GetMapping("/sorted")
    public ResponseEntity<List<ForwardingOrderDTO>> findAllForwardingOrdersSortedBy(@RequestParam String sortBy) {
        Forwarder forwarder = getLoggedInUser();
        List<ForwardingOrderDTO> sortedOrders = forwarderOrderService.findAllForwardingOrdersSortedBy(forwarder, sortBy);
        return ResponseEntity.ok(sortedOrders);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateForwardingOrder(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        ForwardingOrderDTO orderDTO = forwarderOrderService.findForwardingOrderById(id);
        applyPatchAndUpdateOrder(orderDTO, patch);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelForwardingOrder(@PathVariable Long id) {
        forwarderOrderService.cancelForwardingOrder(id);
        return ResponseEntity.noContent().build();
    }

    private void applyPatchAndUpdateOrder(ForwardingOrderDTO orderDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode orderNode = objectMapper.valueToTree(orderDTO);
        JsonNode patchedOrder = patch.apply(orderNode);
        ForwardingOrderDTO patchedOrderDTO = objectMapper.treeToValue(patchedOrder, ForwardingOrderDTO.class);
        forwarderOrderService.updateForwardingOrder(orderDTO, patchedOrderDTO);
    }

    private Forwarder getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return forwarderRepository.findByEmail(userEmail).orElseThrow(() -> new ForwarderNotFoundException("Forwarder not found"));
    }
}
