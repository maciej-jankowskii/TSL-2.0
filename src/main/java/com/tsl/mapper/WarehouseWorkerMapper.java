package com.tsl.mapper;

import com.tsl.dtos.WarehouseWorkerDTO;
import com.tsl.enums.FormOfEmployment;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.exceptions.WarehouseNotFoundException;
import com.tsl.model.address.Address;
import com.tsl.model.employee.WarehouseWorker;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

@Service
public class WarehouseWorkerMapper {
    private final AddressRepository addressRepository;
    private final WarehouseRepository warehouseRepository;

    public WarehouseWorkerMapper(AddressRepository addressRepository, WarehouseRepository warehouseRepository) {
        this.addressRepository = addressRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public WarehouseWorker mapToEntity(WarehouseWorkerDTO dto){
        if (dto == null){
            throw new NullEntityException("Warehouse worker data cannot be null");
        }

        WarehouseWorker worker = new WarehouseWorker();
        worker.setId(dto.getId());
        worker.setFirstName(dto.getFirstName());
        worker.setLastName(dto.getLastName());
        worker.setTelephone(dto.getTelephone());
        Address address = addressRepository.findById(dto.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));
        worker.setAddress(address);
        worker.setBasicGrossSalary(dto.getBasicGrossSalary());
        worker.setDateOfEmployment(dto.getDateOfEmployment());
        worker.setContractExpiryDate(dto.getContractExpiryDate());
        worker.setFormOfEmployment(FormOfEmployment.valueOf(dto.getFormOfEmployment()));
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId()).orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
        worker.setWarehouse(warehouse);
        worker.setPermissionsCrane(dto.getPermissionsCrane());
        worker.setPermissionsForklift(dto.getPermissionsForklift());
        return worker;
    }
    public WarehouseWorkerDTO mapToDTO(WarehouseWorker worker){
        if (worker == null){
            throw new NullEntityException("Warehouse worker cannot be null");
        }

        WarehouseWorkerDTO dto = new WarehouseWorkerDTO();
        dto.setId(worker.getId());
        dto.setFirstName(worker.getFirstName());
        dto.setLastName(worker.getLastName());
        dto.setTelephone(worker.getTelephone());
        dto.setAddressId(worker.getAddress().getId());
        dto.setBasicGrossSalary(worker.getBasicGrossSalary());
        dto.setDateOfEmployment(worker.getDateOfEmployment());
        dto.setContractExpiryDate(worker.getContractExpiryDate());
        dto.setFormOfEmployment(String.valueOf(worker.getFormOfEmployment()));
        dto.setWarehouseId(worker.getWarehouse().getId());
        dto.setPermissionsCrane(worker.getPermissionsCrane());
        dto.setPermissionsForklift(worker.getPermissionsForklift());
        return dto;
    }
}
