package com.tsl.repository;

import com.tsl.model.contact.ContactForm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactFromRepository extends CrudRepository<ContactForm, Long> {
}
