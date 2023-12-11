package com.tsl.controller;

import com.tsl.dtos.ContactPersonDTO;
import com.tsl.service.ContactPersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contact-person")
public class ContactPersonController {
    private final ContactPersonService contactPersonService;

    public ContactPersonController(ContactPersonService contactPersonService) {
        this.contactPersonService = contactPersonService;
    }

    @GetMapping
    public ResponseEntity<List<ContactPersonDTO>> findAllContactPersons(){
        List<ContactPersonDTO> allContactPersons = contactPersonService.findAllContactPersons();
        if (allContactPersons.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allContactPersons);
    }

    @PostMapping
    public ResponseEntity<ContactPersonDTO> addContactPerson(@RequestBody @Valid ContactPersonDTO contactPersonDTO){
        ContactPersonDTO created = contactPersonService.addContactPerson(contactPersonDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
