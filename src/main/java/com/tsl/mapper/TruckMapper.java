package com.tsl.mapper;

import com.tsl.dtos.TruckDTO;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.truck.Truck;
import com.tsl.repository.TransportPlannerRepository;
import org.springframework.stereotype.Service;

@Service
public class TruckMapper {
    private final TransportPlannerRepository transportPlannerRepository;

    public TruckMapper(TransportPlannerRepository transportPlannerRepository) {
        this.transportPlannerRepository = transportPlannerRepository;
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
//        dto.setTransportPlannerId(truck.getTransportPlanner().getId());
        return dto;
    }
}
