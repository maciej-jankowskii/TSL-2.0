package com.tsl.mapper;

import com.tsl.dtos.CarrierDTO;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.contractor.ContactPerson;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierMapper {

    public Carrier mapToEntity(CarrierDTO  carrierDTO){
        if (carrierDTO == null){
            throw new NullEntityException("Carrier data cannot be null");
        }

        Carrier carrier = new Carrier();
        carrier.setId(carrierDTO.getId());
        carrier.setFullName(carrierDTO.getFullName());
        carrier.setShortName(carrierDTO.getShortName());
        carrier.setVatNumber(carrierDTO.getVatNumber());
        carrier.setDescription(carrierDTO.getDescription());
        carrier.setTermOfPayment(carrierDTO.getTermOfPayment());
        carrier.setInsuranceExpirationDate(carrierDTO.getInsuranceExpirationDate());
        carrier.setLicenceExpirationDate(carrier.getInsuranceExpirationDate());
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
