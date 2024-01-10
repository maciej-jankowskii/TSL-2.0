package com.tsl.service.forwardingAndTransport;

import com.tsl.dtos.transport.TransportOrderDTO;
import com.tsl.enums.Currency;
import com.tsl.enums.OrderStatus;
import com.tsl.exceptions.*;
import com.tsl.mapper.TransportOrderMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.order.TransportOrder;
import com.tsl.model.truck.Truck;
import com.tsl.repository.forwardingAndTransport.CargoRepository;
import com.tsl.repository.forwardingAndTransport.TransportOrderRepository;
import com.tsl.repository.employees.TransportPlannerRepository;
import com.tsl.repository.forwardingAndTransport.TruckRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public TransportOrderService(TransportOrderRepository transportOrderRepository,
                                 TransportOrderMapper transportOrderMapper, CargoRepository cargoRepository,
                                 TransportPlannerRepository transportPlannerRepository, TruckRepository truckRepository) {
        this.transportOrderRepository = transportOrderRepository;
        this.transportOrderMapper = transportOrderMapper;
        this.cargoRepository = cargoRepository;
        this.transportPlannerRepository = transportPlannerRepository;
        this.truckRepository = truckRepository;
    }

    /**
     * Finding methods
     */

    public List<TransportOrderDTO> findAllTransportOrders() {
        TransportPlanner planner = getLoggedInUser();
        return transportOrderRepository.findAllByTransportPlannerEmail(planner.getEmail())
                .stream()
                .map(transportOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TransportOrderDTO> findAllTransportOrdersSortedBy(TransportPlanner planner, String sortBy) {
        return transportOrderRepository.findAllTransportOrdersBy(planner.getEmail(), sortBy)
                .stream()
                .map(transportOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public TransportOrderDTO findTransportOrderById(Long id) {
        return transportOrderRepository.findById(id).map(transportOrderMapper::mapToDTO)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    /**
     * Create, update methods
     */

    @Transactional
    public TransportOrderDTO addTransportOrder(TransportOrderDTO dto) {
        TransportOrder order = transportOrderMapper.mapToEntity(dto);
        Truck truck = truckRepository.findById(dto.getTruckId())
                .orElseThrow(() -> new TruckNotFoundException("Truck not found"));
        TransportPlanner planner = getLoggedInUser();

        Cargo cargo = findAndCheckCargo(dto);
        String dtoCurrency = findAndCheckCurrency(dto, cargo);
        order.setCurrency(Currency.valueOf(dtoCurrency));

        checkingDriverOnTruck(truck);
        checkingActualInsuranceAndTechnicalInspection(truck);
        checkingIsCargoAvailable(cargo);
        addAdditionalDataForEntities(order, cargo, planner, truck);

        TransportOrder saved = transportOrderRepository.save(order);
        return transportOrderMapper.mapToDTO(saved);
    }

    private static String findAndCheckCurrency(TransportOrderDTO dto, Cargo cargo) {
        Currency currency = cargo.getCurrency();
        String dtoCurrency = dto.getCurrency();
        if (!currency.equals(Currency.valueOf(dtoCurrency))) {
            throw new CurrencyMismatchException("Currency mismatch");
        }
        return dtoCurrency;
    }

    private Cargo findAndCheckCargo(TransportOrderDTO dto) {
        Cargo cargo = cargoRepository.findById(dto.getCargoId())
                .orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
        if (cargo.getAssignedToOrder()) {
            throw new CargoAlreadyAssignedException("Cargo is already assigned to another order");
        }
        return cargo;
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
    public void cancelTransportOrder(Long id) {
        TransportOrder order = transportOrderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Transport order not found"));
        order.setOrderStatus(OrderStatus.valueOf("CANCELLED"));

    }

    /**
     * Helper methods for add new order
     */

    private static void checkingIsCargoAvailable(Cargo cargo) {
        if (cargo.getAssignedToOrder()) {
            throw new CargoAlreadyAssignedException("Cargo is already assigned to the order");
        }
    }

    private void addAdditionalDataForEntities(TransportOrder order, Cargo cargo, TransportPlanner planner, Truck truck) {
        order.setTruck(truck);
        order.setCargo(cargo);
        order.setTransportPlanner(planner);
        order.setDateAdded(LocalDate.now());
        order.setOrderStatus(OrderStatus.ASSIGNED_TO_COMPANY_TRUCK);
        cargo.setAssignedToOrder(true);
    }

    private TransportPlanner getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return transportPlannerRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EmployeeNotFoundException("Transport planner not found"));
    }

    private static void checkingActualInsuranceAndTechnicalInspection(Truck truck) {
        LocalDate insuranceDate = truck.getInsuranceDate();
        LocalDate technicalInspectionDate = truck.getTechnicalInspectionDate();

        if (insuranceDate.isBefore(LocalDate.now()) || technicalInspectionDate.isBefore(LocalDate.now())) {
            throw new TruckFailsRequirementsException("Truck does not have current technical inspections or current insurance");
        }
    }

    private static void checkingDriverOnTruck(Truck truck) {
        if (!truck.getAssignedToDriver()) {
            throw new NoDriverOnTheTruckException("No driver on this truck");
        }
    }

    /**
     * Helper methods for update order
     */

    private static void checkingUnauthorizedValueChange(TransportOrderDTO currentDTO, TransportOrderDTO updatedDTO) {
        if (currentDTO.getIsInvoiced() == true && updatedDTO.getIsInvoiced() == false) {
            throw new CannotEditEntityException("Cannot change isInvoiced value from true to false");
        }
    }

    private static void checkingInvoicingStatus(TransportOrder order) {
        if (order.getIsInvoiced()) {
            throw new CannotEditEntityException("Cannot edit invoiced order");
        }
    }

    private static void validateTransportPlannerOwnership(TransportPlanner planner, TransportOrder order) {
        if (!order.getTransportPlanner().getId().equals(planner.getId())) {
            throw new CannotEditEntityException("You are not allowed to edit this transport order");
        }
    }
}
