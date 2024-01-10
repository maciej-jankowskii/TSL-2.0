package com.tsl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tsl.dtos.employees.*;
import com.tsl.dtos.forwardiing.CarrierWithBalanceDTO;
import com.tsl.dtos.forwardiing.CustomerWithBalanceDTO;
import com.tsl.dtos.transport.TruckDTO;
import com.tsl.dtos.warehouses.WarehouseDTO;
import com.tsl.service.employees.*;
import com.tsl.service.forwardingAndTransport.CarrierService;
import com.tsl.service.forwardingAndTransport.CustomerService;
import com.tsl.service.forwardingAndTransport.TruckService;
import com.tsl.service.warehouses.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.management.relation.RoleNotFoundException;
import java.net.URI;
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
    private final ObjectMapper objectMapper;
    private final WarehouseService warehouseService;
    private final CustomerService customerService;
    private final CarrierService carrierService;

    public AdminController(ForwarderService forwarderService, TransportPlannerService transportPlannerService,
                           AccountantService accountantService, DriverService driverService,
                           WarehouseWorkerService warehouseWorkerService, TruckService truckService,
                           ObjectMapper objectMapper, WarehouseService warehouseService, CustomerService customerService,
                           CarrierService carrierService) {
        this.forwarderService = forwarderService;
        this.transportPlannerService = transportPlannerService;
        this.accountantService = accountantService;
        this.driverService = driverService;
        this.warehouseWorkerService = warehouseWorkerService;
        this.truckService = truckService;
        this.objectMapper = objectMapper;
        this.warehouseService = warehouseService;
        this.customerService = customerService;
        this.carrierService = carrierService;
    }

    /***
     Handling requests related to find, register and edit new Employees. Add new Trucks and assign Driver to Truck
     */

    @GetMapping("/forwarders")
    public ResponseEntity<List<ForwarderDTO>> findAllForwarders() {
        List<ForwarderDTO> allForwarders = forwarderService.findAllForwarders();
        if (allForwarders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allForwarders);
    }

    @PostMapping("/forwarders/register")
    public ResponseEntity<String> registerNewForwarder(@RequestBody @Valid ForwarderDTO forwarderDTO)
            throws RoleNotFoundException {
        String result = forwarderService.registerNewForwarder(forwarderDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }

    @PatchMapping("/forwarders/{id}")
    public ResponseEntity<?> updateForwarder(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        ForwarderDTO forwarderDTO = forwarderService.findForwarderById(id);
        applyPatchAndUpdateForwarder(forwarderDTO, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/forwarders/{id}")
    ResponseEntity<?> deleteForwarder(@PathVariable Long id) {
        forwarderService.deleteForwarder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/planners")
    public ResponseEntity<List<TransportPlannerDTO>> findAllTransportPlanners() {
        List<TransportPlannerDTO> allTransportPlanners = transportPlannerService.findAllTransportPlanners();
        if (allTransportPlanners.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allTransportPlanners);
    }

    @PostMapping("/planners/register")
    public ResponseEntity<String> registerNewPlanner(@RequestBody @Valid TransportPlannerDTO plannerDTO)
            throws RoleNotFoundException {
        String result = transportPlannerService.registerNewTransportPlanner(plannerDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/planners/{id}")
    public ResponseEntity<?> updatePlanner(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        TransportPlannerDTO plannerDTO = transportPlannerService.findPlannerById(id);
        applyPatchAndUpdatePlanner(plannerDTO, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/planners/{id}")
    ResponseEntity<?> deletePlanner(@PathVariable Long id) {
        transportPlannerService.deletePlanner(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accountants")
    public ResponseEntity<List<AccountantDTO>> findAllAccountants() {
        List<AccountantDTO> allAccountants = accountantService.findAllAccountants();
        if (allAccountants.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allAccountants);
    }

    @PostMapping("/accountants/register")
    public ResponseEntity<String> registerNewAccountant(@RequestBody @Valid AccountantDTO accountantDTO)
            throws RoleNotFoundException {
        String result = accountantService.registerNewAccountant(accountantDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/accountants/{id}")
    public ResponseEntity<?> updateAccountant(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        AccountantDTO accountantDTO = accountantService.findAccountantById(id);
        applyPatchAndUpdateAccountant(accountantDTO, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/accountant/{id}")
    ResponseEntity<?> deleteAccountant(@PathVariable Long id) {
        accountantService.deleteAccountant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<DetailedDriverDTO>> findAllDrivers() {
        List<DetailedDriverDTO> allDrivers = driverService.findAllDriversWithAllInfo();
        if (allDrivers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allDrivers);
    }

    @PostMapping("/drivers/register")
    public ResponseEntity<String> registerNewDriver(@RequestBody @Valid DetailedDriverDTO driverDTO) {
        String result = driverService.registerNewDriver(driverDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/drivers/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        DetailedDriverDTO driverDTO = driverService.findDriverById(id);
        applyPatchAndUpdateDriver(driverDTO, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/drivers/{id}")
    ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse-workers")
    public ResponseEntity<List<WarehouseWorkerDTO>> findAllWarehouseWorkers() {
        List<WarehouseWorkerDTO> allWarehouseWorkers = warehouseWorkerService.findAllWarehouseWorkers();
        if (allWarehouseWorkers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allWarehouseWorkers);
    }

    @PostMapping("/warehouse-workers/register")
    public ResponseEntity<String> registerNewWarehouseWorker(@RequestBody @Valid WarehouseWorkerDTO dto) {
        String result = warehouseWorkerService.registerNewWarehouseWorker(dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/warehouse-workers/{id}")
    public ResponseEntity<?> updateWarehouseWorker(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        WarehouseWorkerDTO warehouseWorkerDTO = warehouseWorkerService.findWarehouseWorkerById(id);
        applyPatchAndUpdateWarehouseWorker(warehouseWorkerDTO, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/warehouse-workers/{id}")
    ResponseEntity<?> deleteWarehouseWorker(@PathVariable Long id) {
        warehouseWorkerService.deleteWarehouseWorker(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trucks")
    public ResponseEntity<List<TruckDTO>> findAllTrucks() {
        List<TruckDTO> allTrucks = truckService.findAllTrucks();
        if (allTrucks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allTrucks);
    }

    @PostMapping("/trucks")
    public ResponseEntity<String> addNewTruck(@RequestBody @Valid TruckDTO truckDTO) {
        String result = truckService.addNewTruck(truckDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/trucks/{id}")
    public ResponseEntity<?> updateTrucks(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        TruckDTO truckDTO = truckService.findTruckById(id);
        applyPatchAndUpdateTruck(truckDTO, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/trucks/{id}")
    ResponseEntity<?> deleteTruck(@PathVariable Long id) {
        truckService.deleteTruck(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/driver/{driverId}/assignTruck/{truckId}")
    public ResponseEntity<?> assignTruckToDriver(@PathVariable Long driverId, @PathVariable Long truckId) {
        driverService.assignTruckToDriver(driverId, truckId);
        return ResponseEntity.noContent().build();
    }

    /***
     Handling requests related to Warehouse Entities
     */

    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseDTO> addWarehouse(@RequestBody @Valid WarehouseDTO warehouseDTO) {
        WarehouseDTO created = warehouseService.addWarehouse(warehouseDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/warehouses/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        WarehouseDTO warehouseDTO = warehouseService.findWarehouseById(id);

        applyPatchAndUpdateWarehouse(warehouseDTO, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/warehouses/{id}")
    ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Handling request related to Customer and Carrier
     */

    @GetMapping("/carriers")
    public ResponseEntity<List<CarrierWithBalanceDTO>> findAllCarriers() {
        List<CarrierWithBalanceDTO> allCarriers = carrierService.findAllCarriersWithBalance();
        if (allCarriers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCarriers);

    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerWithBalanceDTO>> findAllCustomers() {
        List<CustomerWithBalanceDTO> allCustomers = customerService.findAllCustomersWithBalance();
        if (allCustomers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allCustomers);

    }

    /**
     * Helper methods for updates
     */

    private void applyPatchAndUpdateWarehouse(WarehouseDTO warehouseDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode warehouseNode = objectMapper.valueToTree(warehouseDTO);
        JsonNode patchedWarehouse = patch.apply(warehouseNode);
        WarehouseDTO patchedWarehouseDTO = objectMapper.treeToValue(patchedWarehouse, WarehouseDTO.class);
        warehouseService.updateWarehouse(patchedWarehouseDTO);
    }

    private void applyPatchAndUpdateForwarder(ForwarderDTO forwarderDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode forwarderNode = objectMapper.valueToTree(forwarderDTO);
        JsonNode patchedForwarder = patch.apply(forwarderNode);
        ForwarderDTO patchedForwarderDTO = objectMapper.treeToValue(patchedForwarder, ForwarderDTO.class);
        forwarderService.updateForwarder(patchedForwarderDTO);
    }

    private void applyPatchAndUpdatePlanner(TransportPlannerDTO plannerDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode plannerNode = objectMapper.valueToTree(plannerDTO);
        JsonNode patchedPlanner = patch.apply(plannerNode);
        TransportPlannerDTO patchedPlannerDTO = objectMapper.treeToValue(patchedPlanner, TransportPlannerDTO.class);
        transportPlannerService.updatePlanner(patchedPlannerDTO);
    }

    private void applyPatchAndUpdateAccountant(AccountantDTO accountantDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode accountantNode = objectMapper.valueToTree(accountantDTO);
        JsonNode patchedAccountant = patch.apply(accountantNode);
        AccountantDTO patchedAccountantDTO = objectMapper.treeToValue(patchedAccountant, AccountantDTO.class);
        accountantService.updateAccountant(patchedAccountantDTO);
    }

    private void applyPatchAndUpdateDriver(DetailedDriverDTO driverDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode driverNode = objectMapper.valueToTree(driverDTO);
        JsonNode patchedDriver = patch.apply(driverNode);
        DetailedDriverDTO patchedDriverDTO = objectMapper.treeToValue(patchedDriver, DetailedDriverDTO.class);
        driverService.updateDriver(patchedDriverDTO);
    }

    private void applyPatchAndUpdateWarehouseWorker(WarehouseWorkerDTO warehouseWorkerDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode workerNode = objectMapper.valueToTree(warehouseWorkerDTO);
        JsonNode patchedWorker = patch.apply(workerNode);
        WarehouseWorkerDTO patchedWorkerDTO = objectMapper.treeToValue(patchedWorker, WarehouseWorkerDTO.class);
        warehouseWorkerService.updateWarehouseWorker(patchedWorkerDTO);
    }

    private void applyPatchAndUpdateTruck(TruckDTO truckDTO, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        JsonNode truckNode = objectMapper.valueToTree(truckDTO);
        JsonNode patchedTruck = patch.apply(truckNode);
        TruckDTO patchedTruckDTO = objectMapper.treeToValue(patchedTruck, TruckDTO.class);
        truckService.updateTruck(patchedTruckDTO);
    }
}
