package com.tsl.service;

import com.tsl.dtos.CarrierDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.CarrierNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.mapper.CarrierMapper;
import com.tsl.model.address.Address;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.CarrierRepository;
import com.tsl.repository.ContactPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarrierServiceTest {

    @Mock
    private CarrierRepository carrierRepository;
    @Mock
    private CarrierMapper carrierMapper;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ContactPersonRepository contactPersonRepository;

    @InjectMocks
    private CarrierService carrierService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Carriers")
    public void testFindAllCarriers_Success() {
        Carrier carrier1 = prepareFirstCarrier();
        Carrier carrier2 = prepareSecondCarrier();
        CarrierDTO carrierDTO1 = prepareFirstDTO();
        CarrierDTO carrierDTO2 = prepareSecondDTO();
        List<Carrier> carriers = Arrays.asList(carrier1, carrier2);

        when(carrierRepository.findAll()).thenReturn(carriers);
        when(carrierMapper.mapToDTO(carrier1)).thenReturn(carrierDTO1);
        when(carrierMapper.mapToDTO(carrier2)).thenReturn(carrierDTO2);
        List<CarrierDTO> resultCarriers = carrierService.findAllCarriers();

        assertNotNull(resultCarriers);
        assertEquals(resultCarriers.size(), carriers.size());
        verify(carrierRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Carrier by ID")
    public void testFindCarrierById_Success() {
        Carrier carrier = prepareFirstCarrier();
        CarrierDTO carrierDTO = prepareFirstDTO();

        when(carrierRepository.findById(carrierDTO.getId())).thenReturn(Optional.of(carrier));
        when(carrierMapper.mapToDTO(carrier)).thenReturn(carrierDTO);

        CarrierDTO resultCarrier = carrierService.findCarrierById(carrier.getId());

        assertEquals(resultCarrier, carrierDTO);
        verify(carrierRepository, times(1)).findById(carrier.getId());

    }

    @Test
    @DisplayName("Should throw CarrierNotFoundException")
    public void testFindCustomerById_CarrierNotFound() {
        Long carrierId = 1L;

        when(carrierRepository.findById(carrierId)).thenReturn(Optional.empty());

        assertThrows(CarrierNotFoundException.class, () -> carrierService.findCarrierById(carrierId));
    }

    @Test
    @DisplayName("Should add new Carrier")
    public void testAddCarrier_Success() {
        Carrier carrier = prepareFirstCarrier();
        CarrierDTO carrierDTO = prepareFirstDTO();


        when(carrierMapper.mapToEntity(carrierDTO)).thenReturn(carrier);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(new Address()));
        when(contactPersonRepository.findById(1L)).thenReturn(Optional.of(new ContactPerson()));
        when(carrierRepository.save(carrier)).thenReturn(carrier);
        when(carrierMapper.mapToDTO(carrier)).thenReturn(carrierDTO);

        CarrierDTO result = carrierService.addCarrier(carrierDTO);

        verify(carrierRepository, times(1)).save(any());
        assertEquals("ABC SPED", result.getFullName());
    }

    @Test
    @DisplayName("Should throw NullEntityException when adding Carrier with null input")
    public void testAddCarrier_NullInput() {
        CarrierDTO nullCarrier = null;
        when(carrierMapper.mapToEntity(nullCarrier)).thenThrow(new NullEntityException("Carrier data cannot be null"));

        assertThrows(NullEntityException.class, () -> carrierService.addCarrier(nullCarrier));

        verify(carrierRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AddressNotFoundException when adding Carrier with non-existent address ID")
    public void testAddCarrier_NonExistentAddress() {
        CarrierDTO carrierDTO = prepareFirstDTO();

        when(carrierMapper.mapToEntity(carrierDTO)).thenReturn(new Carrier());
        when(addressRepository.findById(carrierDTO.getAddressId())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> carrierService.addCarrier(carrierDTO));

        verify(carrierRepository, never()).save(any());
    }

    private CarrierDTO prepareFirstDTO() {
        CarrierDTO carrierDTO = new CarrierDTO();
        carrierDTO.setId(1L);
        carrierDTO.setFullName("ABC SPED");
        carrierDTO.setAddressId(1L);
        carrierDTO.setContactPersonIds(emptyList());
        return carrierDTO;
    }

    private CarrierDTO prepareSecondDTO() {
        CarrierDTO carrierDTO = new CarrierDTO();
        carrierDTO.setId(2L);
        carrierDTO.setFullName("XYZ SPED");
        return carrierDTO;
    }

    private static Carrier prepareSecondCarrier() {
        Carrier carrier = new Carrier();
        carrier.setId(2L);
        carrier.setFullName("XYZ SPED");
        return carrier;
    }

    private static Carrier prepareFirstCarrier() {
        Carrier carrier = new Carrier();
        carrier.setId(1L);
        carrier.setFullName("ABC SPED");
        return carrier;
    }

}