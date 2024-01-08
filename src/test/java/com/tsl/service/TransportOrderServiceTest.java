package com.tsl.service;

import com.tsl.dtos.TransportOrderDTO;
import com.tsl.enums.Currency;
import com.tsl.enums.OrderStatus;
import com.tsl.exceptions.CannotEditEntityException;
import com.tsl.exceptions.NoDriverOnTheTruckException;
import com.tsl.exceptions.OrderNotFoundException;
import com.tsl.exceptions.TruckFailsRequirementsException;
import com.tsl.mapper.TransportOrderMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.employee.Driver;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.order.TransportOrder;
import com.tsl.model.truck.Truck;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.TransportOrderRepository;
import com.tsl.repository.TransportPlannerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransportOrderServiceTest {
    @Mock
    private TransportOrderRepository transportOrderRepository;
    @Mock
    private TransportOrderMapper transportOrderMapper;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private TransportPlannerRepository transportPlannerRepository;
    @InjectMocks
    private TransportOrderService transportOrderService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Transport Orders for logged in Planner")
    public void testFindAllTransportOrders_Success() {
        TransportPlanner planner = prepareLoggedInPlanner("test@example.com");
        TransportOrderDTO orderDTO1 = prepareTransportOrderDTO(1L);
        TransportOrderDTO orderDTO2 = prepareTransportOrderDTO(2L);
        TransportOrder order1 = prepareFirstTransportOrderEntity(1L);
        TransportOrder order2 = prepareSecondTransportOrderEntity(2L);
        List<TransportOrder> transportOrders = Arrays.asList(order1, order2);

        when(transportPlannerRepository.findByEmail(planner.getEmail())).thenReturn(Optional.of(planner));
        when(transportOrderRepository.findAllByTransportPlannerEmail(planner.getEmail()))
                .thenReturn(transportOrders);
        when(transportOrderMapper.mapToDTO(any())).thenReturn(orderDTO1).thenReturn(orderDTO2);

        List<TransportOrderDTO> resultTransportOrders = transportOrderService.findAllTransportOrders();

        assertNotNull(resultTransportOrders);
        assertEquals(resultTransportOrders.size(), transportOrders.size());
        verify(transportPlannerRepository, times(1)).findByEmail(planner.getEmail());
        verify(transportOrderRepository, times(1)).findAllByTransportPlannerEmail(planner.getEmail());
    }

    @Test
    @DisplayName("Should find Transport Order by ID")
    public void testFindTransportOrderById_Success() {
        TransportOrderDTO orderDTO = prepareTransportOrderDTO(1L);

        when(transportOrderRepository.findById(1L)).thenReturn(Optional.of(prepareFirstTransportOrderEntity(1L)));
        when(transportOrderMapper.mapToDTO(any())).thenReturn(orderDTO);

        TransportOrderDTO resultOrder = transportOrderService.findTransportOrderById(1L);

        assertEquals(resultOrder, orderDTO);
        verify(transportOrderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException")
    public void testFindTransportOrderById_OrderNotFound() {
        Long orderId = 1L;

        when(transportOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> transportOrderService.findTransportOrderById(orderId));
    }

    @Test
    @DisplayName("Should add transport order successfully")
    public void testAddTransportOrder_Success() {
        TransportOrderDTO orderDTO = prepareTransportOrderDTO();
        TransportOrder order = prepareTransportOrder();
        Cargo cargo = prepareCargo();
        TransportPlanner planner = prepareLoggedInPlanner("test@example.com");
        Truck truck = prepareTruck();
        truck.setDrivers(Collections.singletonList(new Driver()));
        order.setTruck(truck);

        when(transportOrderMapper.mapToEntity(orderDTO)).thenReturn(order);
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(java.util.Optional.of(cargo));
        when(transportPlannerRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(planner));
        when(transportOrderRepository.save(order)).thenReturn(order);
        when(transportOrderMapper.mapToDTO(order)).thenReturn(orderDTO);

        TransportOrderDTO resultDTO = transportOrderService.addTransportOrder(orderDTO);

        assertNotNull(resultDTO);
        assertEquals(OrderStatus.ASSIGNED_TO_COMPANY_TRUCK, order.getOrderStatus());
        assertTrue(cargo.getAssignedToOrder());
    }

    @Test
    @DisplayName("Should throw NoDriverOnTheTruckException when adding order with truck without driver")
    public void testAddTransportOrder_NoDriverOnTruck() {
        TransportOrderDTO orderDTO = prepareTransportOrderDTO();
        TransportOrder order = prepareTransportOrder();
        Cargo cargo = prepareCargo();
        TransportPlanner planner = prepareLoggedInPlanner("test@example.com");
        Truck truck = prepareTruck();
        truck.setAssignedToDriver(false);
        order.setTruck(truck);

        when(transportOrderMapper.mapToEntity(orderDTO)).thenReturn(order);
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(java.util.Optional.of(cargo));
        when(transportPlannerRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(planner));

        assertThrows(NoDriverOnTheTruckException.class, () -> transportOrderService.addTransportOrder(orderDTO));

        verify(transportOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw TruckFailsRequirementsException when adding order with truck lacking inspections or insurance")
    public void testAddTransportOrder_TruckFailsRequirements() {
        TransportOrderDTO orderDTO = prepareTransportOrderDTO();
        TransportOrder order = prepareTransportOrder();
        Cargo cargo = prepareCargo();
        TransportPlanner planner = prepareLoggedInPlanner("test@example.com");
        Truck truck = prepareTruck();
        truck.setInsuranceDate(LocalDate.now().minusDays(1));
        order.setTruck(truck);


        when(transportOrderMapper.mapToEntity(orderDTO)).thenReturn(order);
        when(cargoRepository.findById(orderDTO.getCargoId())).thenReturn(java.util.Optional.of(cargo));
        when(transportPlannerRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(planner));

        assertThrows(TruckFailsRequirementsException.class, () -> transportOrderService.addTransportOrder(orderDTO));

        verify(transportOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update transport order successfully")
    public void testUpdateTransportOrder_Success() {
        TransportOrderDTO currentDTO = prepareCurrentDTO();
        TransportOrderDTO updatedDTO = prepareUpdatedDTO();

        TransportPlanner planner = prepareLoggedInPlanner("test@example.com");
        currentDTO.setTransportPlannerId(planner.getId());
        TransportOrder order = prepareTransportOrder();
        order.setTransportPlanner(planner);

        when(transportPlannerRepository.findByEmail(anyString())).thenReturn(Optional.of(planner));
        when(transportOrderMapper.mapToEntity(updatedDTO)).thenReturn(order);
        when(transportOrderRepository.save(order)).thenReturn(order);

        assertDoesNotThrow(() -> transportOrderService.updateTransportOrder(currentDTO, updatedDTO));
    }


    @Test
    @DisplayName("Should throw CannotEditEntityException when changing isInvoiced from true to false")
    public void testUpdateTransportOrder_ChangeIsInvoiced() {
        TransportOrderDTO currentDTO = prepareCurrentDTO();
        TransportOrderDTO updatedDTO = prepareUpdatedDTO();
        TransportPlanner planner = prepareLoggedInPlanner("test@example.com");

        currentDTO.setTransportPlannerId(planner.getId());
        TransportOrder order = prepareTransportOrder();
        order.setTransportPlanner(planner);
        currentDTO.setIsInvoiced(true);
        updatedDTO.setIsInvoiced(false);

        when(transportPlannerRepository.findByEmail(anyString())).thenReturn(Optional.of(planner));
        when(transportOrderMapper.mapToEntity(updatedDTO)).thenReturn(order);

        assertThrows(CannotEditEntityException.class, () -> transportOrderService.updateTransportOrder(currentDTO, updatedDTO));
    }

    @Test
    @DisplayName("Should throw CannotEditEntityException when updating invoiced transport order")
    public void testUpdateTransportOrder_InvoicedOrder() {
        TransportOrderDTO currentDTO = prepareCurrentDTO();
        TransportOrderDTO updatedDTO = prepareUpdatedDTO();
        TransportPlanner planner = prepareLoggedInPlanner("test@example.com");

        currentDTO.setTransportPlannerId(planner.getId());
        TransportOrder order = prepareTransportOrder();
        order.setTransportPlanner(planner);
        updatedDTO.setIsInvoiced(true);
        order.setIsInvoiced(true);

        when(transportOrderRepository.save(any(TransportOrder.class))).thenReturn(order);
        when(transportOrderMapper.mapToEntity(updatedDTO)).thenReturn(order);

        assertThrows(CannotEditEntityException.class, () -> transportOrderService.updateTransportOrder(currentDTO, updatedDTO));
    }

    private TransportOrderDTO prepareCurrentDTO() {
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setId(1L);
        dto.setOrderStatus("ON_LOADING");
        dto.setIsInvoiced(false);
        return dto;
    }

    private TransportOrderDTO prepareUpdatedDTO() {
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setId(1L);
        dto.setOrderStatus("UNLOADED");
        dto.setIsInvoiced(true);
        return dto;
    }


    private Truck prepareTruck() {
        Truck truck = new Truck();
        truck.setId(1L);
        truck.setAssignedToDriver(true);
        truck.setInsuranceDate(LocalDate.now().plusDays(1));
        truck.setTechnicalInspectionDate(LocalDate.now().plusDays(1));
        return truck;
    }

    private TransportOrderDTO prepareTransportOrderDTO() {
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setId(1L);
        dto.setPrice(BigDecimal.valueOf(1000));
        dto.setCurrency("PLN");
        return dto;
    }

    private TransportOrder prepareTransportOrder() {
        TransportOrder order = new TransportOrder();
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

    private TransportPlanner prepareLoggedInPlanner(String userEmail) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);

        TransportPlanner planner = new TransportPlanner();
        planner.setId(1L);
        planner.setEmail(userEmail);

        when(transportPlannerRepository.findByEmail(userEmail)).thenReturn(Optional.of(planner));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return planner;
    }

    private TransportOrderDTO prepareTransportOrderDTO(Long id) {
        TransportOrderDTO dto = new TransportOrderDTO();
        dto.setId(id);
        return dto;
    }

    private TransportOrder prepareFirstTransportOrderEntity(Long id) {
        TransportOrder transportOrder = new TransportOrder();
        transportOrder.setId(id);
        return transportOrder;
    }

    private TransportOrder prepareSecondTransportOrderEntity(Long id) {
        TransportOrder transportOrder = new TransportOrder();
        transportOrder.setId(id);
        return transportOrder;
    }
}