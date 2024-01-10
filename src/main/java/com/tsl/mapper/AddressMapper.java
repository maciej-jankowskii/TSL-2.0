package com.tsl.mapper;

import com.tsl.dtos.addressAndContact.AddressDTO;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.address.Address;
import org.springframework.stereotype.Service;

@Service
public class AddressMapper {
    public Address mapToEntity(AddressDTO addressDTO) {
        if (addressDTO == null) {
            throw new NullEntityException("Address data cannot be null");
        }
        Address address = new Address();
        address.setId(addressDTO.getId());
        address.setCountry(addressDTO.getCountry());
        address.setCity(addressDTO.getCity());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setStreet(addressDTO.getStreet());
        address.setHomeNo(addressDTO.getHomeNo());
        address.setFlatNo(addressDTO.getFlatNo());
        return address;
    }

    public AddressDTO mapToDTO(Address address) {
        if (address == null) {
            throw new NullEntityException("Address cannot be null");
        }
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setCountry(address.getCountry());
        dto.setCity(address.getCity());
        dto.setStreet(address.getStreet());
        dto.setPostalCode(address.getPostalCode());
        dto.setHomeNo(address.getHomeNo());
        dto.setFlatNo(address.getFlatNo());
        return dto;
    }
}
