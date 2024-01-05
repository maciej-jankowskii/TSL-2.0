package com.tsl.service;

import com.tsl.dtos.TransportPlannerDTO;
import com.tsl.exceptions.EmailAddressIsTaken;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.mapper.TransportPlannerMapper;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.role.EmployeeRole;
import com.tsl.model.truck.Truck;
import com.tsl.repository.EmployeeRoleRepository;
import com.tsl.repository.TransportPlannerRepository;
import com.tsl.repository.TruckRepository;
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
    private final TruckRepository truckRepository;
    private final EmployeeRoleRepository employeeRoleRepository;

    public TransportPlannerService(TransportPlannerRepository transportPlannerRepository, TransportPlannerMapper transportPlannerMapper, PasswordEncoder passwordEncoder, SalaryBonusCalculator salaryBonusCalculator, TruckRepository truckRepository, EmployeeRoleRepository employeeRoleRepository) {
        this.transportPlannerRepository = transportPlannerRepository;
        this.transportPlannerMapper = transportPlannerMapper;
        this.passwordEncoder = passwordEncoder;
        this.salaryBonusCalculator = salaryBonusCalculator;
        this.truckRepository = truckRepository;
        this.employeeRoleRepository = employeeRoleRepository;
    }

    public List<TransportPlannerDTO> findAllTransportPlanners(){
        return transportPlannerRepository.findAll().stream().map(transportPlannerMapper::mapToDTO).collect(Collectors.toList());
    }

    public TransportPlannerDTO findPlannerById(Long id) {
        return transportPlannerRepository.findById(id).map(transportPlannerMapper::mapToDTO).orElseThrow(() -> new EmployeeNotFoundException("Transport planner not found"));
    }

    @Transactional
    public void updatePlanner(TransportPlannerDTO plannerDTO) {
        TransportPlanner planner = transportPlannerMapper.mapToEntity(plannerDTO);
        transportPlannerRepository.save(planner);
    }

    public void deletePlanner(Long id) {
        transportPlannerRepository.deleteById(id);
    }

    @Transactional
    public String registerNewTransportPlanner(TransportPlannerDTO transportPlanner) throws RoleNotFoundException {
        checkingAvailabilityOfEmail(transportPlanner);

        TransportPlanner planner = transportPlannerMapper.mapToEntity(transportPlanner);

        addAdditionalDataForTrucks(planner);
        addAdditionalDataForPlanners(transportPlanner, planner);

        transportPlannerRepository.save(planner);
        return "User registered successfully!";
    }

    private void addAdditionalDataForTrucks(TransportPlanner planner) {
        List<Truck> companyTrucks = planner.getCompanyTrucks();
        companyTrucks.forEach(truck -> truck.setTransportPlanner(planner));
    }

    private void addAdditionalDataForPlanners(TransportPlannerDTO plannerDTO, TransportPlanner planner) throws RoleNotFoundException {
        planner.setPassword(passwordEncoder.encode(plannerDTO.getPassword()));
        planner.setSalaryBonus(salaryBonusCalculator.calculateSalaryBonusForPlanners(planner));
        EmployeeRole role = employeeRoleRepository.findByName("PLANNER").orElseThrow(() -> new RoleNotFoundException("Role not found"));
        planner.setRoles(new ArrayList<>(Collections.singletonList(role)));
    }


    private void checkingAvailabilityOfEmail(TransportPlannerDTO transportPlanner) {
        if (transportPlannerRepository.existsByEmail(transportPlanner.getEmail())){
            throw new EmailAddressIsTaken("Email address is already taken");
        }
    }
}
