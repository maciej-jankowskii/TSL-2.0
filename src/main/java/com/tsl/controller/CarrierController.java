package com.tsl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
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
    private final ObjectMapper objectMapper;

    public CarrierController(CarrierService carrierService, ObjectMapper objectMapper) {
        this.carrierService = carrierService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<CarrierDTO>> findAllCarriers() {
        List<CarrierDTO> allCarriers = carrierService.findAllCarriers();
        if (allCarriers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCarriers);
    }

    @PostMapping
    public ResponseEntity<CarrierDTO> addCarrier(@RequestBody @Valid CarrierDTO carrierDTO) {
        CarrierDTO created = carrierService.addCarrier(carrierDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<CarrierDTO>> findAllCarriersSortedBy(@RequestParam String sortBy) {
        List<CarrierDTO> sortedCarriers = carrierService.findAllCarriersSortedBy(sortBy);
        return ResponseEntity.ok(sortedCarriers);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCarrier(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        CarrierDTO carrierDTO = carrierService.findCarrierById(id);
        applyPatchAndUpdateCarrier(carrierDTO, patch);
        return ResponseEntity.noContent().build();

    }

    private void applyPatchAndUpdateCarrier(CarrierDTO carrierDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode carrierNode = objectMapper.valueToTree(carrierDTO);
        JsonNode patchedCarrier = patch.apply(carrierNode);
        CarrierDTO patchedCarrierDTO = objectMapper.treeToValue(patchedCarrier, CarrierDTO.class);
        carrierService.updateCarrier(carrierDTO, patchedCarrierDTO);
    }
}
