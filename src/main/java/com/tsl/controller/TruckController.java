package com.tsl.controller;

import com.tsl.dtos.transport.TruckDTO;
import com.tsl.service.forwardingAndTransport.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;


    /***
     Handling requests related to reading trucks
     */
    @GetMapping
    public ResponseEntity<List<TruckDTO>> findAllTrucks(){
        List<TruckDTO> allTrucks = truckService.findAllTrucks();
        if (allTrucks.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allTrucks);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<TruckDTO>> finAllTruckSortedBy(@RequestParam String sortBy){
        List<TruckDTO> allTrucks = truckService.findAllTruckSortedBy(sortBy);
        return ResponseEntity.ok(allTrucks);
    }
}
