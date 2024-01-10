package com.tsl.mapper;

import com.tsl.dtos.CarrierInvoiceDTO;
import com.tsl.exceptions.CarrierNotFoundException;
import com.tsl.exceptions.OrderNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.invoice.CarrierInvoice;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.repository.CarrierRepository;
import com.tsl.repository.ForwarderOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CarrierInvoiceMapper {

    private final ForwarderOrderRepository forwarderOrderRepository;
    private final CarrierRepository carrierRepository;

    public CarrierInvoiceMapper(ForwarderOrderRepository forwarderOrderRepository, CarrierRepository carrierRepository) {
        this.forwarderOrderRepository = forwarderOrderRepository;
        this.carrierRepository = carrierRepository;
    }

    public CarrierInvoice mapToEntity(CarrierInvoiceDTO carrierInvoiceDTO){
        if (carrierInvoiceDTO == null){
            throw new NullEntityException("Invoice from Carrier data cannot be null");
        }
        CarrierInvoice carrierInvoice = new CarrierInvoice();
        carrierInvoice.setId(carrierInvoiceDTO.getId());
        carrierInvoice.setInvoiceNumber(carrierInvoiceDTO.getInvoiceNumber());
        carrierInvoice.setInvoiceDate(carrierInvoiceDTO.getInvoiceDate());
        carrierInvoice.setDueDate(carrierInvoiceDTO.getDueDate());
        carrierInvoice.setNetValue(carrierInvoiceDTO.getNetValue());
        carrierInvoice.setGrossValue(carrierInvoiceDTO.getGrossValue());
        carrierInvoice.setIsPaid(carrierInvoiceDTO.getIsPaid());
        return carrierInvoice;
    }

    public CarrierInvoiceDTO mapToDTO(CarrierInvoice carrierInvoice){
        if (carrierInvoice == null){
            throw new NullEntityException("Invoice from Carrier cannot be null");
        }

        CarrierInvoiceDTO dto = new CarrierInvoiceDTO();
        dto.setId(carrierInvoice.getId());
        dto.setInvoiceNumber(carrierInvoice.getInvoiceNumber());
        dto.setInvoiceDate(carrierInvoice.getInvoiceDate());
        dto.setDueDate(carrierInvoice.getDueDate());
        dto.setNetValue(carrierInvoice.getNetValue());
        dto.setGrossValue(carrierInvoice.getGrossValue());
        dto.setIsPaid(carrierInvoice.getIsPaid());
        dto.setOrderId(carrierInvoice.getOrder().getId());
        dto.setCarrierId(carrierInvoice.getCarrier().getId());
        return dto;
    }
}
