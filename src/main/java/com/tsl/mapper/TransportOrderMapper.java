package com.tsl.mapper;

import com.tsl.dtos.ForwardingOrderDTO;
import com.tsl.dtos.TransportOrderDTO;
import com.tsl.enums.Currency;
import com.tsl.enums.OrderStatus;
import com.tsl.exceptions.*;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.model.order.TransportOrder;
import com.tsl.model.truck.Truck;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.TransportPlannerRepository;
import com.tsl.repository.TruckRepository;
import org.springframework.stereotype.Service;

@Service
public class TransportOrderMapper {

    private final CargoRepository cargoRepository;
    private final TransportPlannerRepository transportPlannerRepository;
    private final TruckRepository truckRepository;

    public TransportOrderMapper(CargoRepository cargoRepository, TransportPlannerRepository transportPlannerRepository, TruckRepository truckRepository) {
        this.cargoRepository = cargoRepository;
        this.transportPlannerRepository = transportPlannerRepository;
        this.truckRepository = truckRepository;
    }


    public TransportOrder mapToEntity(TransportOrderDTO dto) {
        if (dto == null) {
            throw new NullEntityException("Forwarder order data cannot be null");
        }

        TransportOrder order = new TransportOrder();
        order.setId(dto.getId());
        order.setOrderNumber(dto.getOrderNumber());
        order.setDateAdded(dto.getDateAdded());
        Cargo cargo = cargoRepository.findById(dto.getCargoId()).orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
        if (cargo.getAssignedToOrder()) {
            throw new CargoAlreadyAssignedException("Cargo is already assigned to another order");
        }
        order.setCargo(cargo);
        order.setPrice(dto.getPrice());
        Currency currency = cargo.getCurrency();
        String dtoCurrency = dto.getCurrency();
        if (!currency.equals(Currency.valueOf(dtoCurrency))) {
            throw new CurrencyMismatchException("Currency mismatch");
        }
        order.setCurrency(Currency.valueOf(dtoCurrency));
        order.setIsInvoiced(dto.getIsInvoiced());
        TransportPlanner planner = transportPlannerRepository.findById(dto.getTransportPlannerId()).orElseThrow(() -> new PlannerNotFoundException("Transport planner not found"));
        order.setTransportPlanner(planner);
        Truck truck = truckRepository.findById(dto.getTruckId()).orElseThrow(() -> new TruckNotFoundException("Truck not found"));
        order.setTruck(truck);
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
        dto.setPrice(order.getPrice());
        dto.setCurrency(String.valueOf(order.getCurrency()));
        dto.setTransportPlannerId(order.getTransportPlanner().getId());
        dto.setTruckId(order.getTruck().getId());
        dto.setIsInvoiced(order.getIsInvoiced());
        dto.setOrderStatus(String.valueOf(order.getOrderStatus()));
        return dto;
    }
}
