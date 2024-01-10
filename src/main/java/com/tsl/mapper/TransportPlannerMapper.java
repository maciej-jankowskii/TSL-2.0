package com.tsl.mapper;

import com.tsl.dtos.employees.TransportPlannerDTO;
import com.tsl.enums.FormOfEmployment;
import com.tsl.exceptions.*;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.truck.Truck;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportPlannerMapper {

    public TransportPlanner mapToEntity(TransportPlannerDTO dto) {
        if (dto == null) {
            throw new NullEntityException("Transport planner data cannot be null");
        }
        TransportPlanner planner = new TransportPlanner();
        planner.setId(dto.getId());
        planner.setFirstName(dto.getFirstName());
        planner.setLastName(dto.getLastName());
        planner.setEmail(dto.getEmail());
        planner.setTelephone(dto.getTelephone());
        planner.setPassword(dto.getPassword());
        planner.setBasicGrossSalary(dto.getBasicGrossSalary());
        planner.setDateOfEmployment(dto.getDateOfEmployment());
        planner.setFormOfEmployment(FormOfEmployment.valueOf(dto.getFormOfEmployment()));
        planner.setContractExpiryDate(dto.getContractExpiryDate());
        planner.setSalaryBonus(dto.getSalaryBonus());
        return planner;
    }

    public TransportPlannerDTO mapToDTO(TransportPlanner planner) {
        if (planner == null) {
            throw new NullEntityException("Transport planner cannot be null");
        }
        TransportPlannerDTO dto = new TransportPlannerDTO();
        dto.setId(planner.getId());
        dto.setFirstName(planner.getFirstName());
        dto.setLastName(planner.getLastName());
        dto.setEmail(planner.getEmail());
        dto.setTelephone(planner.getTelephone());
        dto.setPassword(planner.getPassword());
        dto.setAddressId(planner.getAddress().getId());
        dto.setBasicGrossSalary(planner.getBasicGrossSalary());
        dto.setDateOfEmployment(planner.getDateOfEmployment());
        dto.setFormOfEmployment(String.valueOf(planner.getFormOfEmployment()));
        dto.setContractExpiryDate(planner.getContractExpiryDate());
        dto.setSalaryBonus(planner.getSalaryBonus());
        List<Long> truckIds = planner.getCompanyTrucks().stream()
                .map(Truck::getId)
                .collect(Collectors.toList());

        dto.setTruckIds(truckIds);
        return dto;
    }
}
