package com.tsl.service;

import com.tsl.dtos.TransportPlannerDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.EmailAddressIsTaken;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.exceptions.NoTrucksException;
import com.tsl.mapper.TransportPlannerMapper;
import com.tsl.model.address.Address;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.role.EmployeeRole;
import com.tsl.model.truck.Truck;
import com.tsl.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportPlannerService {
    private final TransportPlannerRepository transportPlannerRepository;
    private final TransportPlannerMapper transportPlannerMapper;
    private final PasswordEncoder passwordEncoder;
    private final SalaryBonusCalculator salaryBonusCalculator;
    private final UserRepository userRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final AddressRepository addressRepository;
    private final TruckRepository truckRepository;

    public TransportPlannerService(TransportPlannerRepository transportPlannerRepository, TransportPlannerMapper transportPlannerMapper, PasswordEncoder passwordEncoder, SalaryBonusCalculator salaryBonusCalculator, UserRepository userRepository, EmployeeRoleRepository employeeRoleRepository, AddressRepository addressRepository, TruckRepository truckRepository) {
        this.transportPlannerRepository = transportPlannerRepository;
        this.transportPlannerMapper = transportPlannerMapper;
        this.passwordEncoder = passwordEncoder;
        this.salaryBonusCalculator = salaryBonusCalculator;
        this.userRepository = userRepository;
        this.employeeRoleRepository = employeeRoleRepository;
        this.addressRepository = addressRepository;
        this.truckRepository = truckRepository;
    }

    public List<TransportPlannerDTO> findAllTransportPlanners(){
        return transportPlannerRepository.findAll().stream().map(transportPlannerMapper::mapToDTO).collect(Collectors.toList());
    }

    public TransportPlannerDTO findPlannerById(Long id) {
        return transportPlannerRepository.findById(id).map(transportPlannerMapper::mapToDTO).orElseThrow(() -> new EmployeeNotFoundException("Transport planner not found"));
    }

    @Transactional
    public String registerNewTransportPlanner(TransportPlannerDTO transportPlanner) throws RoleNotFoundException {
        checkingAvailabilityOfEmail(transportPlanner);

        TransportPlanner planner = transportPlannerMapper.mapToEntity(transportPlanner);
        Address address = addressRepository.findById(transportPlanner.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));

        addAdditionalDataForTrucks(planner);
        addAdditionalDataForPlanners(transportPlanner, planner, address);

        transportPlannerRepository.save(planner);
        return "User registered successfully!";
    }

    @Transactional
    public void updatePlanner(TransportPlannerDTO plannerDTO) {
        TransportPlanner planner = transportPlannerMapper.mapToEntity(plannerDTO);
        transportPlannerRepository.save(planner);
    }

    public void deletePlanner(Long id) {
        transportPlannerRepository.deleteById(id);
    }

    private void addAdditionalDataForTrucks(TransportPlanner planner) {
        List<Truck> companyTrucks = planner.getCompanyTrucks();
        companyTrucks.forEach(truck -> truck.setTransportPlanner(planner));
    }

    private void addAdditionalDataForPlanners(TransportPlannerDTO plannerDTO, TransportPlanner planner, Address address) throws RoleNotFoundException {
        planner.setPassword(passwordEncoder.encode(plannerDTO.getPassword()));
        planner.setSalaryBonus(salaryBonusCalculator.calculateSalaryBonusForPlanners(planner));
        EmployeeRole role = employeeRoleRepository.findByName("PLANNER").orElseThrow(() -> new RoleNotFoundException("Role not found"));
        planner.setRoles(new ArrayList<>(Collections.singletonList(role)));

        List<Truck> trucks = plannerDTO.getTruckIds().stream()
                .map(truckIds -> truckRepository.findById(truckIds)
                        .orElseThrow(() -> new NoTrucksException("Truck not found with id: " + truckIds)))
                .collect(Collectors.toList());

        if (trucks.isEmpty()) {
            throw new NoTrucksException("No truck selected");
        }

        planner.setCompanyTrucks(trucks);
        planner.setAddress(address);
    }


    private void checkingAvailabilityOfEmail(TransportPlannerDTO transportPlanner) {
        if (userRepository.existsByEmail(transportPlanner.getEmail())){
            throw new EmailAddressIsTaken("Email address is already taken");
        }
    }
}
