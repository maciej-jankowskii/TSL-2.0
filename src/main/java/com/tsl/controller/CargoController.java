package com.tsl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tsl.dtos.forwardiing.CargoDTO;
import com.tsl.service.forwardingAndTransport.CargoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cargos")
public class CargoController {

    private final CargoService cargoService;
    private final ObjectMapper objectMapper;

    public CargoController(CargoService cargoService, ObjectMapper objectMapper) {
        this.cargoService = cargoService;
        this.objectMapper = objectMapper;
    }

    /***
     Handling requests related to reading, adding, updating, deleting customers
     */

    @GetMapping
    public ResponseEntity<List<CargoDTO>> findAllCargos() {
        List<CargoDTO> allCargos = cargoService.findAllCargos();
        if (allCargos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCargos);
    }

    @PostMapping
    public ResponseEntity<CargoDTO> addCargo(@RequestBody @Valid CargoDTO cargoDTO) {
        CargoDTO created = cargoService.addCargo(cargoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<CargoDTO>> findAllCargosSortedBy(@RequestParam String sortBy) {
        List<CargoDTO> sortedCargos = cargoService.findAllCargosSortedBy(sortBy);
        return ResponseEntity.ok(sortedCargos);
    }

    @GetMapping("/not-assigned")
    public ResponseEntity<List<CargoDTO>> findAllNotAssignedCargos() {
        List<CargoDTO> allNotAssignedCargos = cargoService.findAllNotAssignedCargos();
        if (allNotAssignedCargos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allNotAssignedCargos);
    }

    @GetMapping("/not-invoiced")
    public ResponseEntity<List<CargoDTO>> findAllNotInvoicedCargos() {
        List<CargoDTO> allNotInvoicedCargos = cargoService.findAllNotInvoicedCargos();
        if (allNotInvoicedCargos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allNotInvoicedCargos);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCargo(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        CargoDTO cargoDTO = cargoService.findCargoById(id);
        applyPatchAndUpdateCargo(cargoDTO, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCargo(@PathVariable Long id) {
        cargoService.deleteCargo(id);
        return ResponseEntity.noContent().build();
    }

    /***
     Helper methods for updates
     */

    private void applyPatchAndUpdateCargo(CargoDTO cargoDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode cargoNode = objectMapper.valueToTree(cargoDTO);
        JsonNode patchedCargo = patch.apply(cargoNode);
        CargoDTO patchedCargoDTO = objectMapper.treeToValue(patchedCargo, CargoDTO.class);
        cargoService.updateCargo(cargoDTO, patchedCargoDTO);
    }
}
