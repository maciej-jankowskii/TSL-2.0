package com.tsl.service;

import com.tsl.dtos.AddressDTO;
import com.tsl.mapper.AddressMapper;
import com.tsl.model.address.Address;
import com.tsl.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

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
