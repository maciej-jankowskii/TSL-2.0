package com.tsl.service.forwardingAndTransport;

import com.tsl.dtos.forwardiing.CarrierDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.CarrierNotFoundException;
import com.tsl.exceptions.ContactPersonNotFoundException;
import com.tsl.mapper.CarrierMapper;
import com.tsl.model.address.Address;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.repository.contactAndAddress.AddressRepository;
import com.tsl.repository.forwardingAndTransport.CarrierRepository;
import com.tsl.repository.contactAndAddress.ContactPersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;
    private final AddressRepository addressRepository;
    private final ContactPersonRepository contactPersonRepository;

    public CarrierService(CarrierRepository carrierRepository, CarrierMapper carrierMapper,
                          AddressRepository addressRepository, ContactPersonRepository contactPersonRepository) {
        this.carrierRepository = carrierRepository;
        this.carrierMapper = carrierMapper;
        this.addressRepository = addressRepository;
        this.contactPersonRepository = contactPersonRepository;
    }

    /**
     * Finding methods
     */

    public List<CarrierDTO> findAllCarriers() {
        return carrierRepository.findAll().stream().map(carrierMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<CarrierDTO> findAllCarriersSortedBy(String sortBy) {
        return carrierRepository.findAllCarriersBy(sortBy).stream().map(carrierMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public CarrierDTO findCarrierById(Long id) {
        return carrierRepository.findById(id).map(carrierMapper::mapToDTO)
                .orElseThrow(() -> new CarrierNotFoundException("Carrier not found"));
    }

    /**
     * Create, update methods
     */

    @Transactional
    public CarrierDTO addCarrier(CarrierDTO carrierDTO) {
        Carrier carrier = carrierMapper.mapToEntity(carrierDTO);

        addAdditionalDataForCarrier(carrierDTO, carrier);
        addAdditionalDataForContactPerson(carrier);

        Carrier saved = carrierRepository.save(carrier);
        return carrierMapper.mapToDTO(saved);
    }

    private void addAdditionalDataForCarrier(CarrierDTO carrierDTO, Carrier carrier) {
        carrier.setBalance(BigDecimal.ZERO);

        Address address = addressRepository.findById(carrierDTO.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        carrier.setAddress(address);

        List<ContactPerson> contact = carrierDTO.getContactPersonIds().stream()
                .map(contactPersonIds -> contactPersonRepository.findById(contactPersonIds)
                        .orElseThrow(() -> new ContactPersonNotFoundException("Contact Person not found")))
                .collect(Collectors.toList());
        carrier.setContactPersons(contact);
    }

    @Transactional
    public void updateCarrier(CarrierDTO carrierDTO) {
        Carrier carrier = carrierMapper.mapToEntity(carrierDTO);

        carrierRepository.save(carrier);
    }

    /**
     * Helper methods
     */

    private static void addAdditionalDataForContactPerson(Carrier carrier) {
        List<ContactPerson> contactPersons = carrier.getContactPersons();
        if (!contactPersons.isEmpty()) {
            contactPersons.forEach(person -> person.setContractor(carrier));
        }
    }
}
