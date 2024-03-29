package com.tsl.mapper;

import com.tsl.dtos.employees.DetailedDriverDTO;
import com.tsl.enums.FormOfEmployment;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.employee.Driver;
import org.springframework.stereotype.Service;

@Service
public class DetailedDriverMapper {

    public Driver mapToEntity(DetailedDriverDTO dto) {
        if (dto == null) {
            throw new NullEntityException("Driver data cannot be null");
        }
        Driver driver = new Driver();
        driver.setId(dto.getId());
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setTelephone(dto.getTelephone());
        driver.setBasicGrossSalary(dto.getBasicGrossSalary());
        driver.setDateOfEmployment(dto.getDateOfEmployment());
        driver.setFormOfEmployment(FormOfEmployment.valueOf(dto.getFormOfEmployment()));
        driver.setContractExpiryDate(dto.getContractExpiryDate());
        driver.setLicenceExpiryDate(dto.getLicenceExpiryDate());
        driver.setDriverLicenceNumber(dto.getDriverLicenceNumber());
        driver.setWorkSystem(dto.getWorkSystem());
        driver.setAssignedToTruck(dto.getAssignedToTruck());
        driver.setMainDriver(dto.getMainDriver());
        return driver;
    }

    public DetailedDriverDTO mapToDTO(Driver driver) {
        if (driver == null) {
            throw new NullEntityException("Driver cannot be null");
        }
        DetailedDriverDTO dto = new DetailedDriverDTO();
        dto.setId(driver.getId());
        dto.setFirstName(driver.getFirstName());
        dto.setLastName(driver.getLastName());
        dto.setTelephone(driver.getTelephone());
        dto.setAddressId(driver.getAddress().getId());
        dto.setBasicGrossSalary(driver.getBasicGrossSalary());
        dto.setDateOfEmployment(driver.getDateOfEmployment());
        dto.setFormOfEmployment(String.valueOf(driver.getFormOfEmployment()));
        dto.setContractExpiryDate(driver.getContractExpiryDate());
        dto.setDriverLicenceNumber(driver.getDriverLicenceNumber());
        dto.setLicenceExpiryDate(driver.getLicenceExpiryDate());
        dto.setWorkSystem(driver.getWorkSystem());
        dto.setAssignedToTruck(driver.getAssignedToTruck());
        dto.setMainDriver(driver.getMainDriver());
        dto.setTruckId(driver.getTruck().getId());
        return dto;
    }
}
