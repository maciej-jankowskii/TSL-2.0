package com.tsl.service;

import com.tsl.dtos.WarehouseDTO;
import com.tsl.mapper.WarehouseMapper;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    public List<WarehouseDTO> findAllWarehouses(){
        return warehouseRepository.findAll().stream().map(warehouseMapper::mapToDTO).collect(Collectors.toList());
    }


}
