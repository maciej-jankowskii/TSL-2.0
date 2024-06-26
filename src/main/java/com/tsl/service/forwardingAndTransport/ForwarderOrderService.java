package com.tsl.service.forwardingAndTransport;

import com.tsl.dtos.forwardiing.ForwardingOrderDTO;
import com.tsl.enums.Currency;
import com.tsl.enums.OrderStatus;
import com.tsl.exceptions.*;
import com.tsl.mapper.ForwardingOrderMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.employee.Forwarder;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.repository.forwardingAndTransport.CargoRepository;
import com.tsl.repository.forwardingAndTransport.CarrierRepository;
import com.tsl.repository.forwardingAndTransport.ForwarderOrderRepository;
import com.tsl.repository.employees.ForwarderRepository;
import com.tsl.service.calculators.VatCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForwarderOrderService {
    private final ForwarderOrderRepository forwarderOrderRepository;
    private final ForwardingOrderMapper forwardingOrderMapper;
    private final ForwarderRepository forwarderRepository;
    private final CargoRepository cargoRepository;
    private final CarrierRepository carrierRepository;
    private final VatCalculatorService vatCalculatorService;


    /**
     * Finding methods
     */

    public List<ForwardingOrderDTO> findAllForwardingOrders() {
        Forwarder forwarder = getLoggedInUser();
        return forwarderOrderRepository.findAllByForwarder_Email(forwarder.getEmail())
                .stream()
                .map(forwardingOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ForwardingOrderDTO> findAllForwardingOrdersSortedBy(Forwarder forwarder, String sortBy) {
        return forwarderOrderRepository.findAllForwardingOrdersBy(forwarder.getEmail(), sortBy)
                .stream()
                .map(forwardingOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public ForwardingOrderDTO findForwardingOrderById(Long id) {
        return forwarderOrderRepository.findById(id).map(forwardingOrderMapper::mapToDTO)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    /**
     * Create, update methods
     */

    @Transactional
    public ForwardingOrderDTO addForwardingOrder(ForwardingOrderDTO forwardingOrderDTO) {
        ForwardingOrder order = forwardingOrderMapper.mapToEntity(forwardingOrderDTO);

        Cargo cargo = extractCargoFromOrderDTO(forwardingOrderDTO, order);
        Carrier carrier = extractCarrierFromOrderDTO(forwardingOrderDTO, order);
        checkingCurrency(forwardingOrderDTO, order, cargo);
        Forwarder forwarder = getLoggedInUser();

        checkingActualInsuranceAndLicence(carrier);
        checkingIsCargoAvailable(cargo);
        addAdditionalDataForOrderAndCargo(order, cargo, forwarder);
        changeCarrierBalance(carrier, order);

        ForwardingOrder saved = forwarderOrderRepository.save(order);
        return forwardingOrderMapper.mapToDTO(saved);

    }

    @Transactional
    public void updateForwardingOrder(ForwardingOrderDTO currentDTO, ForwardingOrderDTO updatedDTO) {
        Forwarder forwarder = getLoggedInUser();
        ForwardingOrder order = forwardingOrderMapper.mapToEntity(updatedDTO);

        validateForwarderOwnership(forwarder, order);
        checkingInvoicingStatus(order);
        checkingUnauthorizedValueChange(currentDTO, updatedDTO);
        checkingOrderCancellation(updatedDTO, order);

        forwarderOrderRepository.save(order);
    }

    @Transactional
    public void cancelForwardingOrder(Long id) {
        ForwardingOrder order = forwarderOrderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Forwarding order not found"));
        order.setOrderStatus(OrderStatus.valueOf("CANCELLED"));

        changeCarrierBalance(order);
    }

    /**
     * Helper methods for add new order
     */

    private static void checkingCurrency(ForwardingOrderDTO forwardingOrderDTO, ForwardingOrder order, Cargo cargo) {
        Currency currency = cargo.getCurrency();
        String dtoCurrency = forwardingOrderDTO.getCurrency();

        if (!currency.equals(Currency.valueOf(dtoCurrency))) {
            throw new CurrencyMismatchException("Currency mismatch");
        }
        order.setCurrency(Currency.valueOf(dtoCurrency));
    }

    private Carrier extractCarrierFromOrderDTO(ForwardingOrderDTO forwardingOrderDTO, ForwardingOrder order) {
        Carrier carrier = carrierRepository.findById(forwardingOrderDTO.getCarrierId())
                .orElseThrow(() -> new CarrierNotFoundException("Carrier not found"));
        order.setCarrier(carrier);
        return carrier;
    }

    private Cargo extractCargoFromOrderDTO(ForwardingOrderDTO forwardingOrderDTO, ForwardingOrder order) {
        Cargo cargo = cargoRepository.findById(forwardingOrderDTO.getCargoId())
                .orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
        order.setCargo(cargo);
        return cargo;
    }


    private static void checkingIsCargoAvailable(Cargo cargo) {
        if (cargo.getAssignedToOrder()) {
            throw new CargoAlreadyAssignedException("Cargo is already assigned to another order");
        }
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

    private void changeCarrierBalance(Carrier carrier, ForwardingOrder order) {
        BigDecimal grossPrice = checkingGrossPrice(carrier, order);
        carrier.setBalance(carrier.getBalance().add(grossPrice));
    }

    private BigDecimal checkingGrossPrice(Carrier carrier, ForwardingOrder order) {
        String vatNumber = carrier.getVatNumber();
        BigDecimal orderPrice = order.getPrice();
        return vatCalculatorService.calculateGrossValue(orderPrice, vatNumber);
    }

    private static void checkingActualInsuranceAndLicence(Carrier carrier) {
        LocalDate insuranceExpirationDate = carrier.getInsuranceExpirationDate();
        LocalDate licenceExpirationDate = carrier.getLicenceExpirationDate();
        if (insuranceExpirationDate.isBefore(LocalDate.now()) || licenceExpirationDate.isBefore(LocalDate.now())) {
            throw new CarrierFailsRequirements("Carrier doesnt have actual insurance or licence");
        }
    }

    /**
     * Helper methods for update order
     */


    private static void validateForwarderOwnership(Forwarder forwarder, ForwardingOrder order) {
        if (!order.getForwarder().getId().equals(forwarder.getId())) {
            throw new CannotEditEntityException("You are not allowed to edit this forwarding order");
        }
    }

    private static void checkingUnauthorizedValueChange(ForwardingOrderDTO currentDTO, ForwardingOrderDTO updatedDTO) {
        if (currentDTO.getIsInvoiced() == true && updatedDTO.getIsInvoiced() == false) {
            throw new CannotEditEntityException("Cannot change isInvoiced value from true to false");
        }
    }

    private static void checkingInvoicingStatus(ForwardingOrder order) {
        if (order.getIsInvoiced()) {
            throw new CannotEditEntityException("Cannot edit invoiced order");
        }
    }

    private void changeCarrierBalance(ForwardingOrder order) {
        Carrier carrier = order.getCarrier();
        BigDecimal netValue = order.getPrice();
        BigDecimal grossValue = vatCalculatorService.calculateGrossValue(netValue, carrier.getVatNumber());
        carrier.setBalance(carrier.getBalance().subtract(grossValue));
    }

    private static void checkingOrderCancellation(ForwardingOrderDTO updatedDTO, ForwardingOrder order) {
        if (updatedDTO.getOrderStatus().equals("CANCELLED")) {
            Carrier carrier = order.getCarrier();
            carrier.setBalance(carrier.getBalance().subtract(order.getPrice()));
        }
    }

    private Forwarder getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return forwarderRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EmployeeNotFoundException("Forwarder not found"));
    }
}
