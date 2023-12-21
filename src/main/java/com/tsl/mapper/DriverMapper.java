package com.tsl.mapper;

import com.tsl.dtos.DriverDTO;
import com.tsl.enums.FormOfEmployment;
import com.tsl.exceptions.NullEntityException;
import com.tsl.exceptions.TruckNotFoundException;
import com.tsl.model.employee.Driver;
import com.tsl.model.truck.Truck;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.TruckRepository;
import org.springframework.stereotype.Service;

@Service
public class DriverMapper {

    private final TruckRepository truckRepository;

    public DriverMapper(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    public Driver mapToEntity(DriverDTO dto){
        if (dto == null){
            throw new NullEntityException("Driver data cannot be null");
        }
        Driver driver = new Driver();
        driver.setId(dto.getId());
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setTelephone(dto.getTelephone());

        driver.setDateOfEmployment(dto.getDateOfEmployment());
        driver.setFormOfEmployment(FormOfEmployment.valueOf(dto.getFormOfEmployment()));
        driver.setContractExpiryDate(dto.getContractExpiryDate());
        driver.setLicenceExpiryDate(dto.getLicenceExpiryDate());
        driver.setDriverLicenceNumber(dto.getDriverLicenceNumber());
        driver.setWorkSystem(dto.getWorkSystem());
        Truck truck = truckRepository.findById(dto.getTruckId()).orElseThrow(() -> new TruckNotFoundException("Truck not found"));
        driver.setTruck(truck);
        driver.setAssignedToTruck(dto.getAssignedToTruck());
        driver.setMainDriver(dto.getMainDriver());
        return driver;
    }

    public DriverDTO mapToDTO(Driver driver){
        if (driver == null){
            throw new NullEntityException("Driver cannot be null");
        }
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setFirstName(driver.getFirstName());
        dto.setLastName(driver.getLastName());
        dto.setTelephone(driver.getTelephone());
        dto.setDateOfEmployment(driver.getDateOfEmployment());
        dto.setFormOfEmployment(String.valueOf(driver.getFormOfEmployment()));
        dto.setContractExpiryDate(dto.getContractExpiryDate());
        dto.setDriverLicenceNumber(dto.getDriverLicenceNumber());
        dto.setLicenceExpiryDate(dto.getLicenceExpiryDate());
        dto.setWorkSystem(dto.getWorkSystem());
        dto.setTruckId(dto.getTruckId());
        dto.setAssignedToTruck(dto.getAssignedToTruck());
        dto.setMainDriver(dto.getMainDriver());
        return dto;
    }
}
