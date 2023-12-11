package com.tsl.service;

import com.tsl.dtos.ContactPersonDTO;
import com.tsl.mapper.ContactPersonMapper;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.repository.ContactPersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactPersonService {

    private final ContactPersonRepository contactPersonRepository;
    private final ContactPersonMapper contactPersonMapper;

    public ContactPersonService(ContactPersonRepository contactPersonRepository, ContactPersonMapper contactPersonMapper) {
        this.contactPersonRepository = contactPersonRepository;
        this.contactPersonMapper = contactPersonMapper;
    }

    public List<ContactPersonDTO> findAllContactPersons(){
        return contactPersonRepository.findAll().stream().map(contactPersonMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ContactPersonDTO addContactPerson(ContactPersonDTO contactPersonDTO){
        ContactPerson contactPerson = contactPersonMapper.mapToEntity(contactPersonDTO);
        ContactPerson saved = contactPersonRepository.save(contactPerson);
        return contactPersonMapper.mapToDTO(saved);
    }
}
