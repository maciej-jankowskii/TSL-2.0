package com.tsl.mapper;

import com.tsl.dtos.forwardiing.ForwardingOrderDTO;
import com.tsl.enums.TypeOfTruck;
import com.tsl.exceptions.*;
import com.tsl.model.order.ForwardingOrder;
import org.springframework.stereotype.Service;

@Service
public class ForwardingOrderMapper {

    public ForwardingOrder mapToEntity(ForwardingOrderDTO dto) {
        if (dto == null) {
            throw new NullEntityException("Forwarder order data cannot be null");
        }
        ForwardingOrder order = new ForwardingOrder();
        order.setId(dto.getId());
        order.setOrderNumber(dto.getOrderNumber());
        order.setPrice(dto.getPrice());
        order.setTypeOfTruck(TypeOfTruck.valueOf(dto.getTypeOfTruck()));
        order.setTruckNumbers(dto.getTruckNumbers());
        order.setMargin(dto.getMargin());
        order.setIsInvoiced(dto.getIsInvoiced());
        order.setDateAdded(dto.getDateAdded());
        return order;
    }

    public ForwardingOrderDTO mapToDTO(ForwardingOrder order) {
        if (order == null) {
            throw new NullEntityException("Forwarding order cannot be null");
        }
        ForwardingOrderDTO dto = new ForwardingOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCargoId(order.getCargo().getId());
        dto.setPrice(order.getPrice());
        dto.setCurrency(String.valueOf(order.getCurrency()));
        dto.setCarrierId(order.getCarrier().getId());
        dto.setTypeOfTruck(String.valueOf(order.getTypeOfTruck()));
        dto.setTruckNumbers(order.getTruckNumbers());
        dto.setMargin(order.getMargin());
        dto.setIsInvoiced(order.getIsInvoiced());
        dto.setForwarderId(order.getForwarder().getId());
        dto.setOrderStatus(String.valueOf(order.getOrderStatus()));
        dto.setDateAdded(order.getDateAdded());
        return dto;
    }
}
