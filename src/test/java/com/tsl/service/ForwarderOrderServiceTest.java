package com.tsl.service;

import com.tsl.dtos.ForwardingOrderDTO;
import com.tsl.enums.Currency;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ForwarderOrderServiceTest {

    @Mock
    private ForwarderOrderRepository forwarderOrderRepository;
    @Mock
    private ForwardingOrderMapper forwardingOrderMapper;
    @Mock
    private ForwarderRepository forwarderRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private CarrierRepository carrierRepository;
    @Mock
    private VatCalculatorService vatCalculatorService;
    @InjectMocks
    private ForwarderOrderService forwarderOrderService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Forwarding Orders for logged in Forwarder")
    public void testFindAllForwardingOrders_Success() {
        Forwarder loggedForwarder = prepareLoggedInForwarder("test@example.com");
        ForwardingOrderDTO orderDTO1 = prepareForwardingOrderDTO(1L);
        ForwardingOrderDTO orderDTO2 = prepareForwardingOrderDTO(2L);
        ForwardingOrder order1 = prepareFirstForwardingOrderEntity(1L);
        ForwardingOrder order2 = prepareSecondForwardingOrderEntity(2L);
        List<ForwardingOrder> forwardingOrders = Arrays.asList(order1, order2);

        when(forwarderRepository.findByEmail(loggedForwarder.getEmail())).thenReturn(Optional.of(loggedForwarder));
        when(forwarderOrderRepository.findAllByForwarder_Email(loggedForwarder.getEmail()))
                .thenReturn(forwardingOrders);
        when(forwardingOrderMapper.mapToDTO(any())).thenReturn(orderDTO1).thenReturn(orderDTO2);

        List<ForwardingOrderDTO> resultForwardingOrders = forwarderOrderService.findAllForwardingOrders();

        assertNotNull(resultForwardingOrders);
        assertEquals(resultForwardingOrders.size(), forwardingOrders.size());
        verify(forwarderRepository, times(1)).findByEmail(loggedForwarder.getEmail());
        verify(forwarderOrderRepository, times(1)).findAllByForwarder_Email(loggedForwarder.getEmail());
    }

    @Test
    @DisplayName("Should find Forwarding Order by ID")
    public void testFindForwardingOrderById_Success() {
        ForwardingOrderDTO orderDTO = prepareForwardingOrderDTO(1L);

        when(forwarderOrderRepository.findById(1L)).thenReturn(Optional.of(prepareFirstForwardingOrderEntity(1L)));
        when(forwardingOrderMapper.mapToDTO(any())).thenReturn(orderDTO);

        ForwardingOrderDTO resultOrder = forwarderOrderService.findForwardingOrderById(1L);

        assertEquals(resultOrder, orderDTO);
        verify(forwarderOrderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException")
    public void testFindForwardingOrderById_OrderNotFound() {
        Long orderId = 1L;

        when(forwarderOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> forwarderOrderService.findForwardingOrderById(orderId));
    }

    @Test
    @DisplayName("Should add forwarding order successfully")
    public void testAddForwardingOrder_Success() {
        ForwardingOrderDTO orderDTO = prepareForwardingOrderDTO();
        ForwardingOrder order = prepareForwardingOrder();
        Cargo cargo = prepareCargo();
        Carrier carrier = prepareCarrier();

        order.setForwarder(prepareLoggedInForwarder("test@example.com"));
        orderDTO.setCargoId(cargo.getId());
        orderDTO.setCarrierId(carrier.getId());


        when(forwardingOrderMapper.mapToEntity(orderDTO)).thenReturn(order);
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(Optional.of(cargo));
        when(carrierRepository.findById(orderDTO.getCarrierId())).thenReturn(Optional.of(carrier));
        when(vatCalculatorService.calculateGrossValue(any(), any())).thenReturn(BigDecimal.TEN);
        when(forwarderOrderRepository.save(order)).thenReturn(order);
        when(forwardingOrderMapper.mapToDTO(order)).thenReturn(orderDTO);

        ForwardingOrderDTO resultDTO = forwarderOrderService.addForwardingOrder(orderDTO);

        assertNotNull(resultDTO);
        assertEquals(OrderStatus.ASSIGNED_TO_CARRIER, order.getOrderStatus());
        assertTrue(carrier.getBalance().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(cargo.getAssignedToOrder());
    }

    @Test
    @DisplayName("Should throw CargoAlreadyAssignedException when adding order with already assigned cargo")
    public void testAddForwardingOrder_CargoAlreadyAssigned() {
        ForwardingOrderDTO orderDTO = prepareForwardingOrderDTO();
        ForwardingOrder order = prepareForwardingOrder();
        Cargo cargo = prepareAssignedCargo();
        Carrier carrier = prepareCarrier();

        order.setForwarder(prepareLoggedInForwarder("test@example.com"));
        orderDTO.setCargoId(cargo.getId());
        orderDTO.setCarrierId(carrier.getId());

        when(forwardingOrderMapper.mapToEntity(orderDTO)).thenReturn(new ForwardingOrder());
        when(carrierRepository.findById(orderDTO.getCarrierId())).thenReturn(Optional.of(carrier));
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(Optional.of(cargo));


        assertThrows(CargoAlreadyAssignedException.class, () -> forwarderOrderService.addForwardingOrder(orderDTO));

        verify(forwarderOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CarrierNotFoundException when adding order with non-existent carrier")
    public void testAddForwardingOrder_NonExistentCarrier() {
        ForwardingOrderDTO orderDTO = prepareForwardingOrderDTO();

        when(forwardingOrderMapper.mapToEntity(orderDTO)).thenReturn(new ForwardingOrder());
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(Optional.of(prepareCargo()));
        when(carrierRepository.findById(orderDTO.getCarrierId())).thenReturn(Optional.empty());

        assertThrows(CarrierNotFoundException.class, () -> forwarderOrderService.addForwardingOrder(orderDTO));

        verify(forwarderOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CargoNotFoundException when adding order with non-existent cargo")
    public void testAddForwardingOrder_NonExistentCargo() {
        ForwardingOrderDTO orderDTO = prepareForwardingOrderDTO();

        when(forwardingOrderMapper.mapToEntity(orderDTO)).thenReturn(new ForwardingOrder());
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(Optional.empty());

        assertThrows(CargoNotFoundException.class, () -> forwarderOrderService.addForwardingOrder(orderDTO));

        verify(forwarderOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CurrencyMismatchException when adding order with mismatched currency")
    public void testAddForwardingOrder_CurrencyMismatch() {
        ForwardingOrderDTO orderDTO = prepareForwardingOrderDTO();
        orderDTO.setCurrency("USD");
        Cargo cargo = prepareCargo();
        Carrier carrier = prepareCarrier();

        when(forwardingOrderMapper.mapToEntity(orderDTO)).thenReturn(new ForwardingOrder());
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(Optional.of(cargo));
        when(carrierRepository.findById(orderDTO.getCarrierId())).thenReturn(Optional.of(carrier));

        assertThrows(CurrencyMismatchException.class, () -> forwarderOrderService.addForwardingOrder(orderDTO));

        verify(forwarderOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CarrierFailsRequirements when adding order with carrier lacking insurance or license")
    public void testAddForwardingOrder_CarrierFailsRequirements() {
        ForwardingOrderDTO orderDTO = prepareForwardingOrderDTO();
        Cargo cargo = prepareCargo();
        Carrier carrier = prepareCarrier();
        carrier.setInsuranceExpirationDate(LocalDate.now().minusDays(1));
        Forwarder forwarder = prepareLoggedInForwarder("test@example.com");
        orderDTO.setForwarderId(forwarder.getId());

        when(forwardingOrderMapper.mapToEntity(orderDTO)).thenReturn(new ForwardingOrder());
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(Optional.of(cargo));
        when(carrierRepository.findById(orderDTO.getCarrierId())).thenReturn(Optional.of(carrier));

        assertThrows(CarrierFailsRequirements.class, () -> forwarderOrderService.addForwardingOrder(orderDTO));

        verify(forwarderOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update forwarding order successfully")
    public void testUpdateForwardingOrder_Success() {
        ForwardingOrderDTO currentDTO = prepareCurrentDTO();
        ForwardingOrderDTO updatedDTO = prepareUpdatedDTO();

        Forwarder forwarder = prepareLoggedInForwarder("test@example.com");
        currentDTO.setForwarderId(forwarder.getId());
        ForwardingOrder order = prepareForwardingOrder();
        order.setForwarder(forwarder);

        when(forwarderRepository.findByEmail(anyString())).thenReturn(Optional.of(forwarder));
        when(forwardingOrderMapper.mapToEntity(updatedDTO)).thenReturn(order);
        when(forwarderOrderRepository.save(order)).thenReturn(order);

        assertDoesNotThrow(() -> forwarderOrderService.updateForwardingOrder(currentDTO, updatedDTO));
    }


    @Test
    @DisplayName("Should throw CannotEditEntityException when changing isInvoiced from true to false")
    public void testUpdateForwardingOrder_ChangeIsInvoiced() {
        ForwardingOrderDTO currentDTO = prepareCurrentDTO();
        ForwardingOrderDTO updatedDTO = prepareUpdatedDTO();
        Forwarder forwarder = prepareLoggedInForwarder("test@example.com");
        forwarder.setId(forwarder.getId());
        currentDTO.setForwarderId(forwarder.getId());
        ForwardingOrder order = prepareForwardingOrder();
        order.setForwarder(forwarder);
        currentDTO.setIsInvoiced(true);
        updatedDTO.setIsInvoiced(false);

        when(forwarderRepository.findByEmail(anyString())).thenReturn(Optional.of(forwarder));
        when(forwardingOrderMapper.mapToEntity(updatedDTO)).thenReturn(order);

        assertThrows(CannotEditEntityException.class, () -> forwarderOrderService.updateForwardingOrder(currentDTO, updatedDTO));
    }

    @Test
    @DisplayName("Should throw CannotEditEntityException when updating invoiced forwarding order")
    public void testUpdateForwardingOrder_InvoicedOrder() {
        ForwardingOrderDTO currentDTO = prepareCurrentDTO();
        ForwardingOrderDTO updatedDTO = prepareUpdatedDTO();
        Forwarder forwarder = prepareLoggedInForwarder("test@example.com");
        forwarder.setId(forwarder.getId());
        currentDTO.setForwarderId(forwarder.getId());
        ForwardingOrder order = prepareForwardingOrder();
        order.setForwarder(forwarder);
        updatedDTO.setIsInvoiced(true);
        order.setIsInvoiced(true);

        when(forwarderOrderRepository.save(any(ForwardingOrder.class))).thenReturn(order);
        when(forwardingOrderMapper.mapToEntity(updatedDTO)).thenReturn(order);

        assertThrows(CannotEditEntityException.class, () -> forwarderOrderService.updateForwardingOrder(currentDTO, updatedDTO));
    }

    private ForwardingOrderDTO prepareForwardingOrderDTO() {
        ForwardingOrderDTO dto = new ForwardingOrderDTO();
        dto.setId(1L);
        dto.setPrice(BigDecimal.valueOf(1000));
        dto.setCurrency("PLN");
        return dto;
    }

    private ForwardingOrder prepareForwardingOrder() {
        ForwardingOrder order = new ForwardingOrder();
        order.setId(1L);
        order.setPrice(BigDecimal.valueOf(1000.0));
        order.setCurrency(Currency.valueOf("PLN"));
        order.setIsInvoiced(false);
        return order;
    }

    private Cargo prepareCargo() {
        Cargo cargo = new Cargo();
        cargo.setId(1L);
        cargo.setAssignedToOrder(false);
        cargo.setCurrency(Currency.valueOf("PLN"));
        cargo.setPrice(BigDecimal.valueOf(1200));
        return cargo;
    }

    private Cargo prepareAssignedCargo() {
        Cargo cargo = new Cargo();
        cargo.setId(1L);
        cargo.setCurrency(Currency.valueOf("PLN"));
        cargo.setPrice(BigDecimal.valueOf(1200));
        cargo.setAssignedToOrder(true);
        return cargo;
    }

    private Carrier prepareCarrier() {
        Carrier carrier = new Carrier();
        carrier.setId(2L);
        carrier.setVatNumber("PL123456789");
        carrier.setBalance(BigDecimal.ZERO);
        carrier.setInsuranceExpirationDate(LocalDate.now().plusDays(30));
        carrier.setLicenceExpirationDate(LocalDate.now().plusMonths(6));
        return carrier;
    }

    private Forwarder prepareLoggedInForwarder(String userEmail) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);

        Forwarder user = new Forwarder();
        user.setId(1L);
        user.setEmail(userEmail);
        user.setTotalMargin(BigDecimal.ZERO);
        when(forwarderRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return user;
    }

    private ForwardingOrderDTO prepareForwardingOrderDTO(Long id) {
        ForwardingOrderDTO dto = new ForwardingOrderDTO();
        dto.setId(id);
        return dto;
    }

    private ForwardingOrder prepareFirstForwardingOrderEntity(Long id) {
        ForwardingOrder forwardingOrder = new ForwardingOrder();
        forwardingOrder.setId(id);
        return forwardingOrder;
    }

    private ForwardingOrder prepareSecondForwardingOrderEntity(Long id) {
        ForwardingOrder forwardingOrder = new ForwardingOrder();
        forwardingOrder.setId(id);
        return forwardingOrder;
    }

    private ForwardingOrderDTO prepareCurrentDTO() {
        ForwardingOrderDTO dto = new ForwardingOrderDTO();
        dto.setId(1L);
        dto.setOrderStatus("ON_LOADING");
        dto.setIsInvoiced(false);
        return dto;
    }

    private ForwardingOrderDTO prepareUpdatedDTO() {
        ForwardingOrderDTO dto = new ForwardingOrderDTO();
        dto.setId(1L);
        dto.setOrderStatus("UNLOADED");
        dto.setIsInvoiced(true);
        return dto;
    }


}