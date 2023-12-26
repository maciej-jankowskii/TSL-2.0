package com.tsl.mapper;

import com.tsl.dtos.TruckDTO;
import com.tsl.enums.TypeOfTruck;
import com.tsl.exceptions.NullEntityException;
import com.tsl.exceptions.PlannerNotFoundException;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.truck.Truck;
import com.tsl.repository.TransportPlannerRepository;
import org.springframework.stereotype.Service;

@Service
public class TruckMapper {
    private final TransportPlannerRepository transportPlannerRepository;

    public TruckMapper(TransportPlannerRepository transportPlannerRepository) {
        this.transportPlannerRepository = transportPlannerRepository;
    }

    public Truck mapToEntity(TruckDTO dto){
        if (dto == null){
            throw new NullEntityException("Truck data cannot be null");
        }

        Truck truck = new Truck();
        truck.setId(dto.getId());
        truck.setModel(dto.getModel());
        truck.setBrand(dto.getBrand());
        truck.setType(TypeOfTruck.valueOf(dto.getType()));
        truck.setPlates(dto.getPlates());
        truck.setTechnicalInspectionDate(dto.getTechnicalInspectionDate());
        truck.setInsuranceDate(dto.getInsuranceDate());
        truck.setAssignedToDriver(dto.getAssignedToDriver());
        TransportPlanner planner = transportPlannerRepository.findById(dto.getTransportPlannerId()).orElseThrow(() -> new PlannerNotFoundException("Transport planner not found"));
        truck.setTransportPlanner(planner);
        return truck;
    }



    public TruckDTO mapToDTO(Truck truck){
        if (truck == null){
            throw new NullEntityException("Truck cannot be null");
        }
        TruckDTO dto = new TruckDTO();

        dto.setId(truck.getId());
        dto.setModel(truck.getModel());
        dto.setBrand(truck.getBrand());
        dto.setType(String.valueOf(truck.getType()));
        dto.setPlates(truck.getPlates());
        dto.setTechnicalInspectionDate(truck.getTechnicalInspectionDate());
        dto.setInsuranceDate(truck.getInsuranceDate());
        dto.setAssignedToDriver(truck.getAssignedToDriver());
        if (truck.getTransportPlanner() != null) {
            dto.setTransportPlannerId(truck.getTransportPlanner().getId());
        }
        return dto;
    }
}
