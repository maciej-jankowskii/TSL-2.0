package com.tsl.service;

import com.tsl.dtos.CarrierDTO;
import com.tsl.mapper.CarrierMapper;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.repository.CarrierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    public CarrierService(CarrierRepository carrierRepository, CarrierMapper carrierMapper) {
        this.carrierRepository = carrierRepository;
        this.carrierMapper = carrierMapper;
    }

    public List<CarrierDTO> findAllCarriers(){
        return carrierRepository.findAll().stream().map(carrierMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public CarrierDTO addCarrier(CarrierDTO carrierDTO){
        Carrier carrier = carrierMapper.mapToEntity(carrierDTO);
        carrier.setBalance(BigDecimal.ZERO);

        addAdditionalDataForContactPerson(carrier);

        Carrier saved = carrierRepository.save(carrier);
        return carrierMapper.mapToDTO(saved);
    }

    private static void addAdditionalDataForContactPerson(Carrier carrier) {
        List<ContactPerson> contactPersons = carrier.getContactPersons();
        if (!contactPersons.isEmpty()){
            contactPersons.forEach(person -> person.setContractor(carrier));
        }
    }
}
