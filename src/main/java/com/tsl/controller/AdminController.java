package com.tsl.controller;

import com.tsl.dtos.ForwarderDTO;
import com.tsl.service.ForwarderService;
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

    public AdminController(ForwarderService forwarderService) {
        this.forwarderService = forwarderService;
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
}
