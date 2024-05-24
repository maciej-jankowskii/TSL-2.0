package com.tsl.service.contactAndAddress;

import com.tsl.dtos.addressAndContact.ContactPersonDTO;
import com.tsl.mapper.ContactPersonMapper;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.repository.contactAndAddress.ContactPersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactPersonService {

    private final ContactPersonRepository contactPersonRepository;
    private final ContactPersonMapper contactPersonMapper;


    public List<ContactPersonDTO> findAllContactPersons() {
        return contactPersonRepository.findAll().stream().map(contactPersonMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ContactPersonDTO addContactPerson(ContactPersonDTO contactPersonDTO) {
        ContactPerson contactPerson = contactPersonMapper.mapToEntity(contactPersonDTO);
        ContactPerson saved = contactPersonRepository.save(contactPerson);
        return contactPersonMapper.mapToDTO(saved);
    }
}
