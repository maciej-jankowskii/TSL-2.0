package com.tsl.service.contactAndAddress;

import com.tsl.dtos.addressAndContact.AddressDTO;
import com.tsl.mapper.AddressMapper;
import com.tsl.model.address.Address;
import com.tsl.repository.contactAndAddress.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;


    public List<AddressDTO> findAllAddresses() {
        return addressRepository.findAll().stream().map(addressMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO) {
        Address address = addressMapper.mapToEntity(addressDTO);
        Address saved = addressRepository.save(address);
        return addressMapper.mapToDTO(saved);
    }


}
