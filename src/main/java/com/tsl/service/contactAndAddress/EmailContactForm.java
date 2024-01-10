package com.tsl.service.contactAndAddress;

import com.tsl.model.contact.ContactForm;

public interface EmailContactForm {
    String sendEmail(ContactForm contactForm);
}
