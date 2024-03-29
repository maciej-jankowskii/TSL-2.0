package com.tsl.mapper;

import com.tsl.dtos.employees.ForwarderDTO;
import com.tsl.enums.FormOfEmployment;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.employee.Forwarder;
import org.springframework.stereotype.Service;

@Service
public class ForwarderMapper {

    public ForwarderDTO mapToDTO(Forwarder forwarder) {
        if (forwarder == null) {
            throw new NullEntityException("Forwarder cannot be null");
        }

        ForwarderDTO dto = new ForwarderDTO();
        dto.setId(forwarder.getId());
        dto.setFirstName(forwarder.getFirstName());
        dto.setLastName(forwarder.getLastName());
        dto.setEmail(forwarder.getEmail());
        dto.setTelephone(forwarder.getTelephone());
        dto.setPassword(forwarder.getPassword());
        dto.setAddressId(forwarder.getAddress().getId());
        dto.setBasicGrossSalary(forwarder.getBasicGrossSalary());
        dto.setDateOfEmployment(forwarder.getDateOfEmployment());
        dto.setFormOfEmployment(String.valueOf(forwarder.getFormOfEmployment()));
        dto.setContractExpiryDate(forwarder.getContractExpiryDate());
        dto.setTotalMargin(forwarder.getTotalMargin());
        dto.setSalaryBonus(forwarder.getSalaryBonus());
        return dto;
    }

    public Forwarder mapToEntity(ForwarderDTO forwarderDTO) {
        if (forwarderDTO == null) {
            throw new NullEntityException("Forwarder data cannot be null");
        }
        Forwarder forwarder = new Forwarder();

        forwarder.setId(forwarderDTO.getId());
        forwarder.setFirstName(forwarderDTO.getFirstName());
        forwarder.setLastName(forwarderDTO.getLastName());
        forwarder.setEmail(forwarderDTO.getEmail());
        forwarder.setPassword(forwarderDTO.getPassword());
        forwarder.setTelephone(forwarderDTO.getTelephone());
        forwarder.setBasicGrossSalary(forwarderDTO.getBasicGrossSalary());
        forwarder.setDateOfEmployment(forwarderDTO.getDateOfEmployment());
        forwarder.setFormOfEmployment(FormOfEmployment.valueOf(forwarderDTO.getFormOfEmployment()));
        forwarder.setContractExpiryDate(forwarderDTO.getContractExpiryDate());
        forwarder.setTotalMargin(forwarderDTO.getTotalMargin());
        forwarder.setSalaryBonus(forwarderDTO.getSalaryBonus());
        return forwarder;
    }
}
