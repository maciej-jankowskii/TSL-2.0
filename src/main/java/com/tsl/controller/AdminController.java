package com.tsl.controller;

import com.tsl.dtos.ForwarderDTO;
import com.tsl.dtos.TransportPlannerDTO;
import com.tsl.service.ForwarderService;
import com.tsl.service.TransportPlannerService;
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

    public AdminController(ForwarderService forwarderService, TransportPlannerService transportPlannerService) {
        this.forwarderService = forwarderService;
        this.transportPlannerService = transportPlannerService;
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
}
