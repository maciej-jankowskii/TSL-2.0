package com.tsl.service;

import com.tsl.dtos.DetailedDriverDTO;
import com.tsl.dtos.DriverDTO;
import com.tsl.dtos.TransportPlannerDTO;
import com.tsl.exceptions.DriverIsAlreadyAssignedToTruck;
import com.tsl.exceptions.DriverNotFoundException;
import com.tsl.exceptions.PlannerNotFoundException;
import com.tsl.exceptions.TruckNotFoundException;
import com.tsl.mapper.DetailedDriverMapper;
import com.tsl.mapper.DriverMapper;
import com.tsl.model.employee.Driver;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.truck.Truck;
import com.tsl.repository.DriverRepository;
import com.tsl.repository.TruckRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService{

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final DriverMapper driverMapper;
    private final DetailedDriverMapper detailedDriverMapper;
    private final PasswordEncoder passwordEncoder;

    public DriverService(DriverRepository driverRepository, TruckRepository truckRepository, DriverMapper driverMapper, DetailedDriverMapper detailedDriverMapper, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.truckRepository = truckRepository;
        this.driverMapper = driverMapper;
        this.detailedDriverMapper = detailedDriverMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<DriverDTO> findAllDrivers(){
        return driverRepository.findAll().stream().map(driverMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<DetailedDriverDTO> findAllDriversWithAllInfo(){
        return driverRepository.findAll().stream().map(detailedDriverMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<DriverDTO> findAllDriverSortedBy(String sortBy){
        return driverRepository.findAllDriversBy(sortBy).stream().map(driverMapper::mapToDTO).collect(Collectors.toList());
    }

    public DetailedDriverDTO findDriverById(Long id) {
        return driverRepository.findById(id).map(detailedDriverMapper::mapToDTO).orElseThrow(() -> new DriverNotFoundException("Driver not found"));
    }

    @Transactional
    public void updateDriver(DetailedDriverDTO driverDTO) {
        Driver driver = detailedDriverMapper.mapToEntity(driverDTO);
        driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

    @Transactional
    public String registerNewDriver(DetailedDriverDTO driverDTO){
        Driver driver = detailedDriverMapper.mapToEntity(driverDTO);

        driverRepository.save(driver);
        return "User registered successfully!";
    }


    @Transactional
    public void assignTruckToDriver(Long driverId, Long truckId){
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        Truck truck = truckRepository.findById(truckId).orElseThrow(() -> new TruckNotFoundException("Truck not found"));

        checkAssignToTruck(driver);
        addAdditionalDataForDriverAndTruck(driver, truck);

        driverRepository.save(driver);
        truckRepository.save(truck);

    }

    private static void addAdditionalDataForDriverAndTruck(Driver driver, Truck truck) {
        driver.setTruck(truck);
        driver.setAssignedToTruck(true);
        driver.setMainDriver(true);

        truck.setAssignedToDriver(true);
    }

    private static void checkAssignToTruck(Driver driver) {
        if (driver.getAssignedToTruck()){
            throw new DriverIsAlreadyAssignedToTruck("Driver is already assigned to other truck");
        }
    }
}
