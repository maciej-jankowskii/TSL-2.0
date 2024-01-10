package com.tsl.mapper;

import com.tsl.dtos.AccountantDTO;
import com.tsl.enums.FormOfEmployment;
import com.tsl.enums.TypeOfAccounting;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.address.Address;
import com.tsl.model.employee.Accountant;
import com.tsl.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountantMapper {
    private final AddressRepository addressRepository;

    public AccountantMapper(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AccountantDTO mapToDTO(Accountant accountant){
        if (accountant == null){
            throw new NullEntityException("Accountant cannot be null");
        }
        AccountantDTO dto = new AccountantDTO();
        dto.setId(accountant.getId());
        dto.setFirstName(accountant.getFirstName());
        dto.setLastName(accountant.getLastName());
        dto.setEmail(accountant.getEmail());
        dto.setTelephone(accountant.getTelephone());
        dto.setPassword(accountant.getPassword());
        dto.setAddressId(accountant.getAddress().getId());
        dto.setBasicGrossSalary(accountant.getBasicGrossSalary());
        dto.setDateOfEmployment(accountant.getDateOfEmployment());
        dto.setFormOfEmployment(String.valueOf(accountant.getFormOfEmployment()));
        dto.setContractExpiryDate(accountant.getContractExpiryDate());
        dto.setTypeOfAccounting(String.valueOf(accountant.getTypeOfAccounting()));
        return dto;
    }

    public Accountant mapToEntity(AccountantDTO dto){
        if (dto == null){
            throw new NullEntityException("Accountant data cannot be null");
        }
        Accountant accountant = new Accountant();
        accountant.setId(dto.getId());
        accountant.setFirstName(dto.getFirstName());
        accountant.setLastName(dto.getLastName());
        accountant.setEmail(dto.getEmail());
        accountant.setPassword(dto.getPassword());
        accountant.setTelephone(dto.getTelephone());
        accountant.setBasicGrossSalary(dto.getBasicGrossSalary());
        accountant.setDateOfEmployment(dto.getDateOfEmployment());
        accountant.setFormOfEmployment(FormOfEmployment.valueOf(dto.getFormOfEmployment()));
        accountant.setContractExpiryDate(dto.getContractExpiryDate());
        accountant.setTypeOfAccounting(TypeOfAccounting.valueOf(dto.getTypeOfAccounting()));
        return accountant;
    }
}
