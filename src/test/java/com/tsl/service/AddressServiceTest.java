package com.tsl.service;

import com.tsl.dtos.addressAndContact.AddressDTO;
import com.tsl.mapper.AddressMapper;
import com.tsl.model.address.Address;
import com.tsl.repository.contactAndAddress.AddressRepository;
import com.tsl.service.contactAndAddress.AddressService;
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

class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create Address successfully")
    public void testCreateAddress_Success() {
        AddressDTO addressDTO = prepareAddressDTO();
        Address address = prepareAddress();

        when(addressMapper.mapToEntity(addressDTO)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.mapToDTO(address)).thenReturn(addressDTO);

        AddressDTO resultAddress = addressService.createAddress(addressDTO);

        assertNotNull(resultAddress);
        assertEquals(addressDTO.getId(), resultAddress.getId());
        assertEquals(addressDTO.getStreet(), resultAddress.getStreet());
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    @DisplayName("Should return list of all Addresses")
    public void testFindAllAddresses_Success() {
        List<Address> addresses = prepareAddressList();
        List<AddressDTO> addressDTOs = prepareAddressDTOList();

        when(addressRepository.findAll()).thenReturn(addresses);
        when(addressMapper.mapToDTO(addresses.get(0))).thenReturn(addressDTOs.get(0));
        when(addressMapper.mapToDTO(addresses.get(1))).thenReturn(addressDTOs.get(1));

        List<AddressDTO> resultAddresses = addressService.findAllAddresses();

        assertNotNull(resultAddresses);
        assertEquals(addressDTOs.size(), resultAddresses.size());
        verify(addressRepository, times(1)).findAll();
    }

    private AddressDTO prepareAddressDTO() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(1L);
        addressDTO.setStreet("Main Street");
        return addressDTO;
    }

    private Address prepareAddress() {
        Address address = new Address();
        address.setId(1L);
        address.setStreet("Main Street");
        return address;
    }

    private List<Address> prepareAddressList() {
        Address address1 = new Address();
        address1.setId(1L);
        address1.setStreet("Main Street");

        Address address2 = new Address();
        address2.setId(2L);
        address2.setStreet("Second Street");

        return Arrays.asList(address1, address2);
    }

    private List<AddressDTO> prepareAddressDTOList() {
        AddressDTO addressDTO1 = new AddressDTO();
        addressDTO1.setId(1L);
        addressDTO1.setStreet("Main Street");

        AddressDTO addressDTO2 = new AddressDTO();
        addressDTO2.setId(2L);
        addressDTO2.setStreet("Second Street");

        return Arrays.asList(addressDTO1, addressDTO2);
    }

}