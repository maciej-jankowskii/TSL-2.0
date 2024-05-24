package com.tsl.controller;

import com.tsl.dtos.addressAndContact.ContactPersonDTO;
import com.tsl.service.contactAndAddress.ContactPersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contact-person")
@RequiredArgsConstructor
public class ContactPersonController {
    private final ContactPersonService contactPersonService;


    /***
     Handling requests related to reading and adding contact persons
     */

    @GetMapping
    public ResponseEntity<List<ContactPersonDTO>> findAllContactPersons() {
        List<ContactPersonDTO> allContactPersons = contactPersonService.findAllContactPersons();
        if (allContactPersons.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allContactPersons);
    }

    @PostMapping
    public ResponseEntity<ContactPersonDTO> addContactPerson(@RequestBody @Valid ContactPersonDTO contactPersonDTO) {
        ContactPersonDTO created = contactPersonService.addContactPerson(contactPersonDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }
}
