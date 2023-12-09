package com.tsl.service;

import com.tsl.dtos.WarehouseDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.mapper.WarehouseMapper;
import com.tsl.model.address.Address;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AddressRepository addressRepository;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper, AddressRepository addressRepository) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.addressRepository = addressRepository;
    }

    public List<WarehouseDTO> findAllWarehouses(){
        return warehouseRepository.findAll().stream().map(warehouseMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public WarehouseDTO addWarehouse(WarehouseDTO warehouseDTO){
        Warehouse warehouse = warehouseMapper.mapToEntity(warehouseDTO);

        Address address = addressRepository.findById(warehouseDTO.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));
        warehouse.setAddress(address);

        Warehouse saved = warehouseRepository.save(warehouse);
        return warehouseMapper.mapToDTO(saved);
    }


}
