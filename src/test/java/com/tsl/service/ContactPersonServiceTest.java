package com.tsl.service;

import com.tsl.dtos.ContactPersonDTO;
import com.tsl.mapper.ContactPersonMapper;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.repository.ContactPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactPersonServiceTest {

    @Mock
    private ContactPersonRepository contactPersonRepository;
    @Mock
    private ContactPersonMapper contactPersonMapper;
    @InjectMocks
    private ContactPersonService contactPersonService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should add ContactPerson successfully")
    public void testAddContactPerson_Success() {
        ContactPersonDTO contactPersonDTO = prepareContactPersonDTO();
        ContactPerson contactPerson = prepareContactPerson();

        when(contactPersonMapper.mapToEntity(contactPersonDTO)).thenReturn(contactPerson);
        when(contactPersonRepository.save(contactPerson)).thenReturn(contactPerson);
        when(contactPersonMapper.mapToDTO(contactPerson)).thenReturn(contactPersonDTO);

        ContactPersonDTO resultContactPerson = contactPersonService.addContactPerson(contactPersonDTO);

        assertNotNull(resultContactPerson);
        assertEquals(contactPersonDTO.getId(), resultContactPerson.getId());
        assertEquals(contactPersonDTO.getFirstName(), resultContactPerson.getFirstName());
        verify(contactPersonRepository, times(1)).save(contactPerson);
    }

    @Test
    @DisplayName("Should return list of all ContactPersons")
    public void testFindAllContactPersons_Success() {
        List<ContactPerson> contactPersons = prepareContactPersonList();
        List<ContactPersonDTO> contactPersonDTOs = prepareContactPersonDTOList();

        when(contactPersonRepository.findAll()).thenReturn(contactPersons);
        when(contactPersonMapper.mapToDTO(contactPersons.get(0))).thenReturn(contactPersonDTOs.get(0));
        when(contactPersonMapper.mapToDTO(contactPersons.get(1))).thenReturn(contactPersonDTOs.get(1));

        List<ContactPersonDTO> resultContactPersons = contactPersonService.findAllContactPersons();

        assertNotNull(resultContactPersons);
        assertEquals(contactPersonDTOs.size(), resultContactPersons.size());
        verify(contactPersonRepository, times(1)).findAll();
    }

    private ContactPersonDTO prepareContactPersonDTO() {
        ContactPersonDTO contactPersonDTO = new ContactPersonDTO();
        contactPersonDTO.setId(1L);
        contactPersonDTO.setFirstName("John Doe");
        return contactPersonDTO;
    }

    private ContactPerson prepareContactPerson() {
        ContactPerson contactPerson = new ContactPerson();
        contactPerson.setId(1L);
        contactPerson.setFirstName("John Doe");
        return contactPerson;
    }

    private List<ContactPerson> prepareContactPersonList() {
        ContactPerson contactPerson1 = new ContactPerson();
        contactPerson1.setId(1L);
        contactPerson1.setFirstName("John Doe");

        ContactPerson contactPerson2 = new ContactPerson();
        contactPerson2.setId(2L);
        contactPerson2.setFirstName("Jane Doe");

        return Arrays.asList(contactPerson1, contactPerson2);
    }

    private List<ContactPersonDTO> prepareContactPersonDTOList() {
        ContactPersonDTO contactPersonDTO1 = new ContactPersonDTO();
        contactPersonDTO1.setId(1L);
        contactPersonDTO1.setFirstName("John Doe");

        ContactPersonDTO contactPersonDTO2 = new ContactPersonDTO();
        contactPersonDTO2.setId(2L);
        contactPersonDTO2.setFirstName("Jane Doe");

        return Arrays.asList(contactPersonDTO1, contactPersonDTO2);
    }

}