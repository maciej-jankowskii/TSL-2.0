package com.tsl.controller;

import com.tsl.dtos.CargoDTO;
import com.tsl.service.CargoService;
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

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping
    public ResponseEntity<List<CargoDTO>> findAllCargos(){
        List<CargoDTO> allCargos = cargoService.findAllCargos();
        if (allCargos.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCargos);
    }

    @PostMapping
    public ResponseEntity<CargoDTO> addCargo(@RequestBody @Valid CargoDTO cargoDTO){
        CargoDTO created = cargoService.addCargo(cargoDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
