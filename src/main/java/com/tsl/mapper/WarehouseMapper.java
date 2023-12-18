package com.tsl.mapper;

import com.tsl.dtos.WarehouseDTO;
import com.tsl.enums.TypeOfGoods;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.address.Address;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class WarehouseMapper {
    private final AddressRepository addressRepository;

    public WarehouseMapper(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Warehouse mapToEntity (WarehouseDTO warehouseDTO){
        if (warehouseDTO == null){
            throw new NullEntityException("Warehouse data cannot be null");
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseDTO.getId());
        warehouse.setTypeOfGoods(TypeOfGoods.valueOf(warehouseDTO.getTypeOfGoods()));
        warehouse.setCrane(warehouseDTO.getCrane());
        warehouse.setForklift(warehouseDTO.getForklift());
        warehouse.setAvailableArea(warehouseDTO.getAvailableArea());
        warehouse.setCostPer100SquareMeters(warehouseDTO.getCostPer100SquareMeters());
        return warehouse;
    }

    public WarehouseDTO mapToDTO(Warehouse warehouse){
        if (warehouse == null){
            throw new NullEntityException("Warehouse cannot be null");
        }
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        if (warehouse.getAddress() != null) {
            dto.setAddressId(warehouse.getAddress().getId());
        }
        dto.setTypeOfGoods(String.valueOf(warehouse.getTypeOfGoods()));
        dto.setCostPer100SquareMeters(warehouse.getCostPer100SquareMeters());
        dto.setAvailableArea(warehouse.getAvailableArea());
        dto.setCrane(warehouse.getCrane());
        dto.setForklift(warehouse.getForklift());
        return dto;
    }
}
