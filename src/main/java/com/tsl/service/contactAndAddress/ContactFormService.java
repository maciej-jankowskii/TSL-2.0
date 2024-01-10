package com.tsl.service.contactAndAddress;

import com.tsl.model.contact.ContactForm;
import com.tsl.repository.contactAndAddress.ContactFromRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
public class ContactFormService implements EmailContactForm {
    private final JavaMailSender javaMailSender;
    private final ContactFromRepository contactFromRepository;

    public ContactFormService(JavaMailSender javaMailSender, ContactFromRepository contactFromRepository) {
        this.javaMailSender = javaMailSender;
        this.contactFromRepository = contactFromRepository;
    }

    @Override
    @Transactional
    public String sendEmail(ContactForm contactForm) throws MailException {

        createAndSaveContactFromData(contactForm);
        SimpleMailMessage message = sendEmailMessage(contactForm);

        javaMailSender.send(message);
        return "Message sent successfully.";
    }

    /**
     * Helper methods for send email method
     */

    private static SimpleMailMessage sendEmailMessage(ContactForm contactForm) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("testtslapp@gmail.com");
        message.setTo("testtslapp@gmail.com");
        message.setSubject(contactForm.getSubject());
        message.setText("Wiadomość od: " + contactForm.getEmail() + "\n" + "Treść: " + contactForm.getMessage());
        return message;
    }

    private void createAndSaveContactFromData(ContactForm contactForm) {
        ContactForm form = new ContactForm();
        form.setName(contactForm.getName());
        form.setSubject(contactForm.getSubject());
        form.setEmail(contactForm.getEmail());
        form.setMessage(contactForm.getMessage());
        form.setDate(LocalDate.now());
        contactFromRepository.save(form);
    }

}
