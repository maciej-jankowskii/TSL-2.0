package com.tsl.service;

import com.tsl.dtos.ForwardingOrderDTO;
import com.tsl.enums.OrderStatus;
import com.tsl.exceptions.CargoNotFoundException;
import com.tsl.exceptions.CarrierNotFoundException;
import com.tsl.exceptions.ForwarderNotFoundException;
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
        return forwarderOrderRepository.findAll().stream().map(forwardingOrderMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ForwardingOrderDTO addForwardingOrder(ForwardingOrderDTO forwardingOrderDTO) {
        ForwardingOrder order = forwardingOrderMapper.mapToEntity(forwardingOrderDTO);
        Cargo cargo = extractCargoFromOrderDTO(forwardingOrderDTO);
        Carrier carrier = extractCarrierFromOrderDTO(forwardingOrderDTO);
        Forwarder forwarder = getLoggedInUser();

        addAdditionalDataForOrderAndCargo(order, cargo, forwarder);
        changeCarrierBalance(carrier, order);

        ForwardingOrder saved = forwarderOrderRepository.save(order);
        return forwardingOrderMapper.mapToDTO(saved);

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
