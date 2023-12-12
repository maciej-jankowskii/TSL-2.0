package com.tsl.mapper;

import com.tsl.dtos.CarrierDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.ContactPersonNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.address.Address;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.contractor.ContactPerson;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.ContactPersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierMapper {
    private final AddressRepository addressRepository;
    private final ContactPersonRepository contactPersonRepository;

    public CarrierMapper(AddressRepository addressRepository, ContactPersonRepository contactPersonRepository) {
        this.addressRepository = addressRepository;
        this.contactPersonRepository = contactPersonRepository;
    }

    public Carrier mapToEntity(CarrierDTO  carrierDTO){
        if (carrierDTO == null){
            throw new NullEntityException("Carrier data cannot be null");
        }

        Carrier carrier = new Carrier();
        carrier.setId(carrierDTO.getId());
        carrier.setFullName(carrierDTO.getFullName());
        carrier.setShortName(carrierDTO.getShortName());
        Address address = addressRepository.findById(carrierDTO.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));
        carrier.setAddress(address);
        carrier.setVatNumber(carrierDTO.getVatNumber());
        carrier.setDescription(carrierDTO.getDescription());
        carrier.setTermOfPayment(carrierDTO.getTermOfPayment());
        carrier.setInsuranceExpirationDate(carrierDTO.getInsuranceExpirationDate());
        carrier.setLicenceExpirationDate(carrier.getInsuranceExpirationDate());
        List<ContactPerson> contact = carrierDTO.getContactPersonIds().stream()
                .map(contactPersonIds -> contactPersonRepository.findById(contactPersonIds)
                        .orElseThrow(() -> new ContactPersonNotFoundException("Contact Person not found with this ID " + contactPersonIds)))
                .collect(Collectors.toList());
        carrier.setContactPersons(contact);
        return carrier;
    }

    public CarrierDTO mapToDTO(Carrier carrier){
        if (carrier == null){
            throw new NullEntityException("Carrier cannot be null");
        }
        CarrierDTO dto = new CarrierDTO();
        dto.setId(carrier.getId());
        dto.setFullName(carrier.getFullName());
        dto.setShortName(carrier.getShortName());
        dto.setAddressId(carrier.getAddress().getId());
        dto.setVatNumber(carrier.getVatNumber());
        dto.setDescription(carrier.getDescription());
        dto.setTermOfPayment(carrier.getTermOfPayment());
        dto.setInsuranceExpirationDate(carrier.getInsuranceExpirationDate());
        dto.setLicenceExpirationDate(carrier.getLicenceExpirationDate());

        List<Long> contactPersons = carrier.getContactPersons().stream().map(ContactPerson::getId).collect(Collectors.toList());
        dto.setContactPersonIds(contactPersons);
        return dto;
    }
}
