package com.tsl.mapper;

import com.tsl.dtos.ContactPersonDTO;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.contractor.ContactPerson;
import org.springframework.stereotype.Service;

@Service
public class ContactPersonMapper {
    public ContactPerson mapToEntity(ContactPersonDTO contactPersonDTO) {
        if (contactPersonDTO == null){
            throw new NullEntityException("Contact person data cannot be null");
        }
        ContactPerson contactPerson = new ContactPerson();
        contactPerson.setId(contactPersonDTO.getId());
        contactPerson.setFirstName(contactPersonDTO.getFirstName());
        contactPerson.setLastName(contactPersonDTO.getLastName());
        contactPerson.setEmail(contactPersonDTO.getEmail());
        contactPerson.setTelephone(contactPersonDTO.getTelephone());
        return contactPerson;
    }

    public ContactPersonDTO mapToDTO(ContactPerson contactPerson) {
        if (contactPerson == null){
            throw new NullEntityException("Contact person cannot be null");
        }
        ContactPersonDTO dto = new ContactPersonDTO();
        dto.setId(contactPerson.getId());
        dto.setFirstName(contactPerson.getFirstName());
        dto.setLastName(contactPerson.getLastName());
        dto.setEmail(contactPerson.getEmail());
        dto.setTelephone(contactPerson.getTelephone());
        return dto;
    }


}
