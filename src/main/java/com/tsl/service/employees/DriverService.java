package com.tsl.service.employees;

import com.tsl.dtos.employees.DetailedDriverDTO;
import com.tsl.dtos.employees.DriverDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.DriverIsAlreadyAssignedToTruck;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.exceptions.TruckNotFoundException;
import com.tsl.mapper.DetailedDriverMapper;
import com.tsl.mapper.DriverMapper;
import com.tsl.model.address.Address;
import com.tsl.model.employee.Driver;
import com.tsl.model.truck.Truck;
import com.tsl.repository.contactAndAddress.AddressRepository;
import com.tsl.repository.employees.DriverRepository;
import com.tsl.repository.forwardingAndTransport.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final DriverMapper driverMapper;
    private final DetailedDriverMapper detailedDriverMapper;
    private final AddressRepository addressRepository;

    public DriverService(DriverRepository driverRepository, TruckRepository truckRepository,
                         DriverMapper driverMapper, DetailedDriverMapper detailedDriverMapper,
                         AddressRepository addressRepository) {
        this.driverRepository = driverRepository;
        this.truckRepository = truckRepository;
        this.driverMapper = driverMapper;
        this.detailedDriverMapper = detailedDriverMapper;
        this.addressRepository = addressRepository;
    }

    /**
     * Finding methods
     */

    public List<DriverDTO> findAllDrivers() {
        return driverRepository.findAll().stream().map(driverMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<DetailedDriverDTO> findAllDriversWithAllInfo() {
        return driverRepository.findAll().stream().map(detailedDriverMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<DriverDTO> findAllDriverSortedBy(String sortBy) {
        return driverRepository.findAllDriversBy(sortBy).stream().map(driverMapper::mapToDTO).collect(Collectors.toList());
    }

    public DetailedDriverDTO findDriverById(Long id) {
        return driverRepository.findById(id).map(detailedDriverMapper::mapToDTO).orElseThrow(() -> new EmployeeNotFoundException("Driver not found"));
    }

    /**
     * Create, update and delete methods
     */

    @Transactional
    public String registerNewDriver(DetailedDriverDTO driverDTO) {
        Driver driver = detailedDriverMapper.mapToEntity(driverDTO);

        Address address = addressRepository.findById(driverDTO.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));
        driver.setAddress(address);

        driverRepository.save(driver);
        return "User registered successfully!";
    }

    @Transactional
    public void assignTruckToDriver(Long driverId, Long truckId) {
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new EmployeeNotFoundException("Driver not found"));
        Truck truck = truckRepository.findById(truckId).orElseThrow(() -> new TruckNotFoundException("Truck not found"));

        checkAssignToTruck(driver);
        addAdditionalDataForDriverAndTruck(driver, truck);

        truckRepository.save(truck);
        driverRepository.save(driver);
    }

    @Transactional
    public void updateDriver(DetailedDriverDTO driverDTO) {
        Driver driver = detailedDriverMapper.mapToEntity(driverDTO);
        driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

    /**
     * Helper methods
     */

    private static void addAdditionalDataForDriverAndTruck(Driver driver, Truck truck) {
        driver.setTruck(truck);
        driver.setAssignedToTruck(true);
        driver.setMainDriver(true);

        truck.setAssignedToDriver(true);
    }

    private static void checkAssignToTruck(Driver driver) {
        if (driver.getAssignedToTruck()) {
            throw new DriverIsAlreadyAssignedToTruck("Driver is already assigned to other truck");
        }
    }
}
