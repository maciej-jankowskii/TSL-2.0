package com.tsl.mapper;

import com.tsl.dtos.TransportPlannerDTO;
import com.tsl.enums.FormOfEmployment;
import com.tsl.exceptions.*;
import com.tsl.model.address.Address;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.truck.Truck;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.TruckRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportPlannerMapper {
    private final AddressRepository addressRepository;
    private final TruckRepository truckRepository;

    public TransportPlannerMapper(AddressRepository addressRepository, TruckRepository truckRepository) {
        this.addressRepository = addressRepository;
        this.truckRepository = truckRepository;
    }

    public TransportPlanner mapToEntity(TransportPlannerDTO dto){
        if (dto == null){
            throw new NullEntityException("Transport planner data cannot be null");
        }
        TransportPlanner planner = new TransportPlanner();
        planner.setId(dto.getId());
        planner.setFirstName(dto.getFirstName());
        planner.setLastName(dto.getLastName());
        planner.setEmail(dto.getEmail());
        planner.setTelephone(dto.getTelephone());
        planner.setPassword(dto.getPassword());
        Address address = addressRepository.findById(dto.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));
        planner.setAddress(address);
        planner.setBasicGrossSalary(dto.getBasicGrossSalary());
        planner.setDateOfEmployment(dto.getDateOfEmployment());
        planner.setFormOfEmployment(FormOfEmployment.valueOf(dto.getFormOfEmployment()));
        planner.setContractExpiryDate(dto.getContractExpiryDate());
        planner.setSalaryBonus(dto.getSalaryBonus());
        List<Truck> trucks = dto.getTruckIds().stream()
                .map(truckIds -> truckRepository.findById(truckIds)
                        .orElseThrow(() -> new NoTrucksException("Truck not found with id: " + truckIds)))
                .collect(Collectors.toList());

        if (trucks.isEmpty()) {
            throw new NoTrucksException("No truck selected");
        }

        planner.setCompanyTrucks(trucks);
        return planner;
    }

    public TransportPlannerDTO mapToDTO(TransportPlanner planner){
        if (planner == null){
            throw new NullEntityException("Transport planner cannot be null");
        }
        TransportPlannerDTO dto = new TransportPlannerDTO();
        dto.setId(planner.getId());
        dto.setFirstName(planner.getFirstName());
        dto.setLastName(planner.getLastName());
        dto.setEmail(planner.getEmail());
        dto.setTelephone(planner.getTelephone());
        dto.setPassword(planner.getPassword());
        dto.setAddressId(planner.getAddress().getId());
        dto.setBasicGrossSalary(planner.getBasicGrossSalary());
        dto.setDateOfEmployment(planner.getDateOfEmployment());
        dto.setFormOfEmployment(String.valueOf(planner.getFormOfEmployment()));
        dto.setContractExpiryDate(planner.getContractExpiryDate());
        dto.setSalaryBonus(planner.getSalaryBonus());
        List<Long> truckIds = planner.getCompanyTrucks().stream()
                .map(Truck::getId)
                .collect(Collectors.toList());

        dto.setTruckIds(truckIds);
        return dto;
    }
}
