package com.tsl.service;

import com.tsl.dtos.ForwardingOrderDTO;
import com.tsl.enums.OrderStatus;
import com.tsl.exceptions.*;
import com.tsl.mapper.ForwardingOrderMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.employee.Forwarder;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.CarrierRepository;
import com.tsl.repository.ForwarderOrderRepository;
import com.tsl.repository.ForwarderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForwarderOrderService {
    private final ForwarderOrderRepository forwarderOrderRepository;
    private final ForwardingOrderMapper forwardingOrderMapper;
    private final ForwarderRepository forwarderRepository;
    private final CargoRepository cargoRepository;
    private final CarrierRepository carrierRepository;
    private final VatCalculatorService vatCalculatorService;

    public ForwarderOrderService(ForwarderOrderRepository forwarderOrderRepository, ForwardingOrderMapper forwardingOrderMapper, ForwarderRepository forwarderRepository, CargoRepository cargoRepository, CarrierRepository carrierRepository, VatCalculatorService vatCalculatorService) {
        this.forwarderOrderRepository = forwarderOrderRepository;
        this.forwardingOrderMapper = forwardingOrderMapper;
        this.forwarderRepository = forwarderRepository;
        this.cargoRepository = cargoRepository;
        this.carrierRepository = carrierRepository;
        this.vatCalculatorService = vatCalculatorService;
    }

    public List<ForwardingOrderDTO> findAllForwardingOrders() {
        Forwarder forwarder = getLoggedInUser();
        return forwarderOrderRepository.findAllByForwarder_Email(forwarder.getEmail())
                .stream()
                .map(forwardingOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ForwardingOrderDTO> findAllForwardingOrdersSortedBy(Forwarder forwarder, String sortBy){
        return forwarderOrderRepository.findAllForwardingOrdersBy(forwarder.getEmail(), sortBy)
                .stream()
                .map(forwardingOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public ForwardingOrderDTO findForwardingOrderById(Long id){
        return forwarderOrderRepository.findById(id).map(forwardingOrderMapper::mapToDTO).orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    @Transactional
    public void updateForwardingOrder(ForwardingOrderDTO currentDTO, ForwardingOrderDTO updatedDTO) {
        Forwarder forwarder = getLoggedInUser();
        ForwardingOrder order = forwardingOrderMapper.mapToEntity(updatedDTO);
        validateForwarderOwnership(forwarder, order);
        checkingInvoicingStatus(order);
        checkingUnauthorizedValueChange(currentDTO, updatedDTO);

        forwarderOrderRepository.save(order);
    }

    private static void validateForwarderOwnership(Forwarder forwarder, ForwardingOrder order) {
        if (!order.getForwarder().getId().equals(forwarder.getId())){
            throw new CannotEditEntityException("You are not allowed to edit this forwarding order");
        }
    }

    private static void checkingUnauthorizedValueChange(ForwardingOrderDTO currentDTO, ForwardingOrderDTO updatedDTO) {
        if (currentDTO.getIsInvoiced() == true && updatedDTO.getIsInvoiced() == false) {
            throw new CannotEditEntityException("Cannot change isInvoiced value from true to false");
        }
    }

    private static void checkingInvoicingStatus(ForwardingOrder order) {
        if (order.getIsInvoiced()){
            throw new CannotEditEntityException("Cannot edit invoiced order");
        }
    }

    @Transactional
    public ForwardingOrderDTO addForwardingOrder(ForwardingOrderDTO forwardingOrderDTO) {
        ForwardingOrder order = forwardingOrderMapper.mapToEntity(forwardingOrderDTO);
        Cargo cargo = extractCargoFromOrderDTO(forwardingOrderDTO);
        Carrier carrier = extractCarrierFromOrderDTO(forwardingOrderDTO);
        Forwarder forwarder = getLoggedInUser();

        checkingIsCargoAvailable(cargo);
        addAdditionalDataForOrderAndCargo(order, cargo, forwarder);
        changeCarrierBalance(carrier, order);

        ForwardingOrder saved = forwarderOrderRepository.save(order);
        return forwardingOrderMapper.mapToDTO(saved);

    }

    private static void checkingIsCargoAvailable(Cargo cargo) {
        if (cargo.getAssignedToOrder()) {
            throw new CargoAlreadyAssignedException("Cargo is already assigned to another order");
        }
    }

    private void changeCarrierBalance(Carrier carrier, ForwardingOrder order) {
        BigDecimal grossPrice = checkingGrossPrice(carrier, order);
        carrier.setBalance(carrier.getBalance().add(grossPrice));
    }

    private BigDecimal checkingGrossPrice(Carrier carrier, ForwardingOrder order) {
        String vatNumber = carrier.getVatNumber();
        BigDecimal orderPrice = order.getPrice();
        return vatCalculatorService.calculateGrossValue(orderPrice, vatNumber);
    }

    private static void addAdditionalDataForOrderAndCargo(ForwardingOrder order, Cargo cargo, Forwarder forwarder) {
        order.setForwarder(forwarder);
        order.setDateAdded(LocalDate.now());
        order.setOrderStatus(OrderStatus.ASSIGNED_TO_CARRIER);
        order.setIsInvoiced(false);

        BigDecimal cargoPrice = cargo.getPrice();
        BigDecimal orderPrice = order.getPrice();
        order.setMargin(cargoPrice.subtract(orderPrice));
        forwarder.setTotalMargin(forwarder.getTotalMargin().add(cargoPrice.subtract(orderPrice)));
        cargo.setAssignedToOrder(true);
    }

    private Carrier extractCarrierFromOrderDTO(ForwardingOrderDTO forwardingOrderDTO) {
        Long carrierId = forwardingOrderDTO.getCarrierId();
        return carrierRepository.findById(carrierId).orElseThrow(() -> new CarrierNotFoundException("Carrier not found"));
    }

    private Cargo extractCargoFromOrderDTO(ForwardingOrderDTO forwardingOrderDTO) {
        Long cargoId = forwardingOrderDTO.getCargoId();
        return cargoRepository.findById(cargoId).orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
    }

    private Forwarder getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return forwarderRepository.findByEmail(userEmail).orElseThrow(() -> new ForwarderNotFoundException("Forwarder not found"));
    }
}
