package com.tsl.controller;

import com.tsl.model.contact.ContactForm;
import com.tsl.service.contactAndAddress.ContactFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactFormController {

    private final ContactFormService contactFormService;


    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid ContactForm contactForm) {
        String sent = contactFormService.sendEmail(contactForm);
        return new ResponseEntity<>(sent, HttpStatus.OK);
    }
}
