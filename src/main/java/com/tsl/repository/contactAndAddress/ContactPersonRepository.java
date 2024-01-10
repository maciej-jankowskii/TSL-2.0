package com.tsl.repository.contactAndAddress;

import com.tsl.model.contractor.ContactPerson;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactPersonRepository extends CrudRepository<ContactPerson, Long> {

    Optional<ContactPerson> findById(Long id);
    List<ContactPerson> findAll();
}
