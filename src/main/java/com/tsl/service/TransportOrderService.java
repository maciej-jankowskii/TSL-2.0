package com.tsl.service;

import com.tsl.dtos.TransportOrderDTO;
import com.tsl.enums.OrderStatus;
import com.tsl.exceptions.CargoNotFoundException;
import com.tsl.exceptions.PlannerNotFoundException;
import com.tsl.mapper.TransportOrderMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.model.employee.TransportPlanner;
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
        return transportOrderRepository.findAll().stream().map(transportOrderMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public TransportOrderDTO addTransportOrder(TransportOrderDTO dto){
        TransportOrder order = transportOrderMapper.mapToEntity(dto);
        Cargo cargo = cargoRepository.findById(dto.getCargoId()).orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
        TransportPlanner planner = getLoggedInUser();
        addAdditionalDataForEntities(order, cargo, planner);
        Customer customer = cargo.getCustomer();
        changeCustomerBalance(customer, order);

        TransportOrder saved = transportOrderRepository.save(order);
        return transportOrderMapper.mapToDTO(saved);
    }

    private void changeCustomerBalance(Customer customer, TransportOrder order){
        BigDecimal grossPrice = checkingGrossPrice(customer, order);
        customer.setBalance(customer.getBalance().add(grossPrice));

    }

    private BigDecimal checkingGrossPrice(Customer customer, TransportOrder order) {
        String vatNumber = customer.getVatNumber();
        BigDecimal orderPrice = order.getPrice();
        return vatCalculatorService.calculateGrossValue(orderPrice, vatNumber);
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
