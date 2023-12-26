package com.tsl.controller;

import com.tsl.dtos.*;
import com.tsl.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ForwarderService forwarderService;
    private final TransportPlannerService transportPlannerService;
    private final AccountantService accountantService;
    private final DriverService driverService;
    private final WarehouseWorkerService warehouseWorkerService;
    private final TruckService truckService;

    public AdminController(ForwarderService forwarderService, TransportPlannerService transportPlannerService, AccountantService accountantService, DriverService driverService, WarehouseWorkerService warehouseWorkerService, TruckService truckService) {
        this.forwarderService = forwarderService;
        this.transportPlannerService = transportPlannerService;
        this.accountantService = accountantService;
        this.driverService = driverService;
        this.warehouseWorkerService = warehouseWorkerService;
        this.truckService = truckService;
    }

    @GetMapping("/forwarders")
    public ResponseEntity<List<ForwarderDTO>> findAllForwarders(){
        List<ForwarderDTO> allForwarders = forwarderService.findAllForwarders();
        if (allForwarders.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allForwarders);
    }

    @PostMapping("/forwarders/register")
    public ResponseEntity<String> registerNewForwarder(@RequestBody @Valid ForwarderDTO forwarderDTO) throws RoleNotFoundException {
        String result = forwarderService.registerNewForwarder(forwarderDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
    @GetMapping("/planners")
    public ResponseEntity<List<TransportPlannerDTO>> findAllTransportPlanners(){
        List<TransportPlannerDTO> allTransportPlanners = transportPlannerService.findAllTransportPlanners();
        if (allTransportPlanners.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allTransportPlanners);
    }

    @PostMapping("/planners/register")
    public ResponseEntity<String> registerNewPlanner(@RequestBody @Valid TransportPlannerDTO plannerDTO) throws RoleNotFoundException {
        String result = transportPlannerService.registerNewTransportPlanner(plannerDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/accountants")
    public ResponseEntity<List<AccountantDTO>> findAllAccountants(){
        List<AccountantDTO> allAccountants = accountantService.findAllAccountants();
        if (allAccountants.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allAccountants);
    }

    @PostMapping("/accountants/register")
    public ResponseEntity<String> registerNewAccountant(@RequestBody @Valid AccountantDTO accountantDTO) throws RoleNotFoundException {
        String result = accountantService.registerNewAccountant(accountantDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<DetailedDriverDTO>> findAllDrivers(){
        List<DetailedDriverDTO> allDrivers = driverService.findAllDriversWithAllInfo();
        if (allDrivers.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allDrivers);
    }

    @PostMapping("/drivers/register")
    public ResponseEntity<String> registerNewDriver(@RequestBody @Valid DetailedDriverDTO driverDTO){
        String result = driverService.registerNewDriver(driverDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/warehouse-workers")
    public ResponseEntity<List<WarehouseWorkerDTO>> findAllWarehouseWorkers(){
        List<WarehouseWorkerDTO> allWarehouseWorkers = warehouseWorkerService.findAllWarehouseWorkers();
        if (allWarehouseWorkers.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allWarehouseWorkers);
    }

    @PostMapping("/warehouse-workers/register")
    public ResponseEntity<String> registerNewWarehouseWorker(@RequestBody @Valid WarehouseWorkerDTO dto){
        String result = warehouseWorkerService.registerNewWarehouseWorker(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/trucks")
    public ResponseEntity<List<TruckDTO>> findAllTrucks(){
        List<TruckDTO> allTrucks = truckService.findAllTrucks();
        if (allTrucks.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allTrucks);
    }

    @PostMapping("/trucks")
    public ResponseEntity<String> addNewTruck(@RequestBody @Valid TruckDTO truckDTO){
        String result = truckService.addNewTruck(truckDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
