package com.tsl.mapper;

import com.tsl.dtos.transport.TransportOrderDTO;
import com.tsl.exceptions.*;
import com.tsl.model.order.TransportOrder;
import org.springframework.stereotype.Service;

@Service
public class TransportOrderMapper {

    public TransportOrder mapToEntity(TransportOrderDTO dto) {
        if (dto == null) {
            throw new NullEntityException("Forwarder order data cannot be null");
        }

        TransportOrder order = new TransportOrder();
        order.setId(dto.getId());
        order.setOrderNumber(dto.getOrderNumber());
        order.setDateAdded(dto.getDateAdded());
        order.setIsInvoiced(dto.getIsInvoiced());
        return order;
    }

    public TransportOrderDTO mapToDTO(TransportOrder order) {
        if (order == null) {
            throw new NullEntityException("Forwarding order cannot be null");
        }
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setDateAdded(order.getDateAdded());
        dto.setCargoId(order.getCargo().getId());
        dto.setPrice(order.getCargo().getPrice());
        dto.setCurrency(String.valueOf(order.getCargo().getCurrency()));
        dto.setTransportPlannerId(order.getTransportPlanner().getId());
        dto.setTruckId(order.getTruck().getId());
        dto.setIsInvoiced(order.getIsInvoiced());
        dto.setOrderStatus(String.valueOf(order.getOrderStatus()));
        return dto;
    }
}
