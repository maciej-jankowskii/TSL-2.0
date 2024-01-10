package com.tsl.mapper;

import com.tsl.dtos.forwardiing.CarrierWithBalanceDTO;
import com.tsl.model.contractor.Carrier;
import org.springframework.stereotype.Service;

@Service
public class CarrierWithBalanceMapper {

    public CarrierWithBalanceDTO mapToDTO(Carrier carrier){
        CarrierWithBalanceDTO dto = new CarrierWithBalanceDTO();
        dto.setId(carrier.getId());
        dto.setFullName(carrier.getFullName());
        dto.setVatNumber(carrier.getVatNumber());
        dto.setBalance(carrier.getBalance());
        return dto;
    }
}
