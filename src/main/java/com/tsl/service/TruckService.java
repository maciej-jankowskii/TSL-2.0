package com.tsl.service;

import com.tsl.dtos.TruckDTO;
import com.tsl.exceptions.PlannerNotFoundException;
import com.tsl.mapper.TruckMapper;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.truck.Truck;
import com.tsl.repository.TransportPlannerRepository;
import com.tsl.repository.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TruckService {
    private final TruckRepository truckRepository;
    private final TruckMapper truckMapper;
    private final TransportPlannerRepository transportPlannerRepository;
    private final SalaryBonusCalculator salaryBonusCalculator;

    public TruckService(TruckRepository truckRepository, TruckMapper truckMapper, TransportPlannerRepository transportPlannerRepository, SalaryBonusCalculator salaryBonusCalculator) {
        this.truckRepository = truckRepository;
        this.truckMapper = truckMapper;
        this.transportPlannerRepository = transportPlannerRepository;
        this.salaryBonusCalculator = salaryBonusCalculator;
    }

    public List<TruckDTO> findAllTrucks(){
        return truckRepository.findAll().stream().map(truckMapper::mapToDTO).collect(Collectors.toList());
    }
    public List<TruckDTO> findAllTruckSortedBy(String sortBy){
        return truckRepository.findAllTrucksBy(sortBy).stream().map(truckMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public String addNewTruck(TruckDTO truckDTO){
        Truck truck = truckMapper.mapToEntity(truckDTO);
        truckRepository.save(truck);

        updateTransportPlannerSalaryBonus(truckDTO);

        return "Truck added successfully!";

    }

    private void updateTransportPlannerSalaryBonus(TruckDTO truckDTO) {
        if (truckDTO.getTransportPlannerId() != null) {
            TransportPlanner planner = transportPlannerRepository.findById(truckDTO.getTransportPlannerId()).orElseThrow(() -> new PlannerNotFoundException("Transport planner not found"));
            planner.setSalaryBonus(salaryBonusCalculator.calculateSalaryBonusForPlanners(planner));
            transportPlannerRepository.save(planner);
        }
    }
}
