package com.tsl.controller;

import com.tsl.dtos.DriverDTO;
import com.tsl.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    /***
     Handling requests related to reading drivers
     */
    @GetMapping
    public ResponseEntity<List<DriverDTO>> findAllDrivers() {
        List<DriverDTO> allDrivers = driverService.findAllDrivers();
        if (allDrivers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allDrivers);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<DriverDTO>> findAllDriverSortedBy(@RequestParam String sortBy) {
        List<DriverDTO> allDrivers = driverService.findAllDriverSortedBy(sortBy);
        return ResponseEntity.ok(allDrivers);
    }
}
