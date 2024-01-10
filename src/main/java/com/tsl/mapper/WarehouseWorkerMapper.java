package com.tsl.mapper;

import com.tsl.dtos.employees.WarehouseWorkerDTO;
import com.tsl.enums.FormOfEmployment;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.employee.WarehouseWorker;
import org.springframework.stereotype.Service;

@Service
public class WarehouseWorkerMapper {
    public WarehouseWorker mapToEntity(WarehouseWorkerDTO dto) {
        if (dto == null) {
            throw new NullEntityException("Warehouse worker data cannot be null");
        }

        WarehouseWorker worker = new WarehouseWorker();
        worker.setId(dto.getId());
        worker.setFirstName(dto.getFirstName());
        worker.setLastName(dto.getLastName());
        worker.setTelephone(dto.getTelephone());
        worker.setBasicGrossSalary(dto.getBasicGrossSalary());
        worker.setDateOfEmployment(dto.getDateOfEmployment());
        worker.setContractExpiryDate(dto.getContractExpiryDate());
        worker.setFormOfEmployment(FormOfEmployment.valueOf(dto.getFormOfEmployment()));
        worker.setPermissionsCrane(dto.getPermissionsCrane());
        worker.setPermissionsForklift(dto.getPermissionsForklift());
        return worker;
    }

    public WarehouseWorkerDTO mapToDTO(WarehouseWorker worker) {
        if (worker == null) {
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
