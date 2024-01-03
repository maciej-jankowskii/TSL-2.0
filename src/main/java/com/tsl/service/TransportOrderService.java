package com.tsl.service;

import com.tsl.dtos.TransportOrderDTO;
import com.tsl.enums.OrderStatus;
import com.tsl.exceptions.*;
import com.tsl.mapper.TransportOrderMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.contractor.Customer;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.model.order.TransportOrder;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.TransportOrderRepository;
import com.tsl.repository.TransportPlannerRepository;
import com.tsl.repository.TruckRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportOrderService {
    private final TransportOrderRepository transportOrderRepository;
    private final TransportOrderMapper transportOrderMapper;
    private final CargoRepository cargoRepository;
    private final TransportPlannerRepository transportPlannerRepository;
    private final TruckRepository truckRepository;
    private final VatCalculatorService vatCalculatorService;

    public TransportOrderService(TransportOrderRepository transportOrderRepository, TransportOrderMapper transportOrderMapper, CargoRepository cargoRepository, TransportPlannerRepository transportPlannerRepository, TruckRepository truckRepository, VatCalculatorService vatCalculatorService) {
        this.transportOrderRepository = transportOrderRepository;
        this.transportOrderMapper = transportOrderMapper;
        this.cargoRepository = cargoRepository;
        this.transportPlannerRepository = transportPlannerRepository;
        this.truckRepository = truckRepository;
        this.vatCalculatorService = vatCalculatorService;
    }

    public List<TransportOrderDTO> findAllTransportOrders(){
        TransportPlanner planner = getLoggedInUser();
        return transportOrderRepository.findAllByTransportPlannerEmail(planner.getEmail())
                .stream()
                .map(transportOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransportOrderDTO addTransportOrder(TransportOrderDTO dto){
        TransportOrder order = transportOrderMapper.mapToEntity(dto);
        Cargo cargo = cargoRepository.findById(dto.getCargoId()).orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
        TransportPlanner planner = getLoggedInUser();

        checkingIsCargoAvailable(cargo);
        addAdditionalDataForEntities(order, cargo, planner);

        TransportOrder saved = transportOrderRepository.save(order);
        return transportOrderMapper.mapToDTO(saved);
    }

    public List<TransportOrderDTO> findAllTransportOrdersSortedBy(TransportPlanner planner, String sortBy){
        return transportOrderRepository.findAllTransportOrdersBy(planner.getEmail(), sortBy)
                .stream()
                .map(transportOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public TransportOrderDTO findTransportOrderById(Long id){
        return transportOrderRepository.findById(id).map(transportOrderMapper::mapToDTO).orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    @Transactional
    public void updateTransportOrder(TransportOrderDTO currentDTO, TransportOrderDTO updatedDTO) {
        TransportPlanner planner = getLoggedInUser();
        TransportOrder order = transportOrderMapper.mapToEntity(updatedDTO);

        validateTransportPlannerOwnership(planner, order);
        checkingInvoicingStatus(order);
        checkingUnauthorizedValueChange(currentDTO, updatedDTO);

        transportOrderRepository.save(order);
    }

    @Transactional
    public void cancelTransportOrder(Long id){
        TransportOrder order = transportOrderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Transport order not found"));
        order.setOrderStatus(OrderStatus.valueOf("CANCELLED"));

    }

    private static void checkingUnauthorizedValueChange(TransportOrderDTO currentDTO, TransportOrderDTO updatedDTO) {
        if (currentDTO.getIsInvoiced() == true && updatedDTO.getIsInvoiced() == false) {
            throw new CannotEditEntityException("Cannot change isInvoiced value from true to false");
        }
    }

    private static void checkingInvoicingStatus(TransportOrder order) {
        if (order.getIsInvoiced()){
            throw new CannotEditEntityException("Cannot edit invoiced order");
        }
    }

    private static void validateTransportPlannerOwnership(TransportPlanner planner, TransportOrder order) {
        if (!order.getTransportPlanner().getId().equals(planner.getId())){
            throw new CannotEditEntityException("You are not allowed to edit this transport order");
        }
    }

    private static void checkingIsCargoAvailable(Cargo cargo) {
        if (cargo.getAssignedToOrder()){
            throw new CargoAlreadyAssignedException("Cargo is already assigned to the order");
        }
    }


    private void addAdditionalDataForEntities(TransportOrder order, Cargo cargo, TransportPlanner planner) {
        order.setTransportPlanner(planner);
        order.setDateAdded(LocalDate.now());
        order.setOrderStatus(OrderStatus.ASSIGNED_TO_COMPANY_TRUCK);
        cargo.setAssignedToOrder(true);

    }


    private TransportPlanner getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return transportPlannerRepository.findByEmail(userEmail).orElseThrow(() -> new PlannerNotFoundException("Transport planner not found"));
    }




}
