package com.tsl.service;

import com.tsl.dtos.CarrierDTO;
import com.tsl.exceptions.CarrierNotFoundException;
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

    public List<CarrierDTO> findAllCarriers() {
        return carrierRepository.findAll().stream().map(carrierMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<CarrierDTO> findAllCarriersSortedBy(String sortBy) {
        return carrierRepository.findAllCarriersBy(sortBy).stream().map(carrierMapper::mapToDTO).collect(Collectors.toList());
    }

    public CarrierDTO findCarrierById(Long id) {
        return carrierRepository.findById(id).map(carrierMapper::mapToDTO).orElseThrow(() -> new CarrierNotFoundException("Carrier not found"));
    }

    @Transactional
    public CarrierDTO addCarrier(CarrierDTO carrierDTO) {
        Carrier carrier = carrierMapper.mapToEntity(carrierDTO);
        carrier.setBalance(BigDecimal.ZERO);

        addAdditionalDataForContactPerson(carrier);

        Carrier saved = carrierRepository.save(carrier);
        return carrierMapper.mapToDTO(saved);
    }

    @Transactional
    public void updateCarrier(CarrierDTO currentDTO, CarrierDTO updatedDTO) {
        Carrier carrier = carrierMapper.mapToEntity(updatedDTO);

        carrierRepository.save(carrier);
    }

    private static void addAdditionalDataForContactPerson(Carrier carrier) {
        List<ContactPerson> contactPersons = carrier.getContactPersons();
        if (!contactPersons.isEmpty()) {
            contactPersons.forEach(person -> person.setContractor(carrier));
        }
    }
}
