package com.tsl.service;

import com.tsl.model.contact.ContactForm;

public interface EmailContactForm {
    String sendEmail(ContactForm contactForm);
}
