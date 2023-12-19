package com.tsl.controller;

import com.tsl.model.contact.ContactForm;
import com.tsl.service.ContactFormService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contact")
public class ContactFormController {

    private final ContactFormService contactFormService;

    public ContactFormController(ContactFormService contactFormService) {
        this.contactFormService = contactFormService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid ContactForm contactForm) {
        String sent = contactFormService.sendEmail(contactForm);
        return new ResponseEntity<>(sent, HttpStatus.OK);
    }
}
