package com.tsl.service;

import com.tsl.dtos.transport.TruckDTO;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.exceptions.TruckNotFoundException;
import com.tsl.mapper.TruckMapper;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.truck.Truck;
import com.tsl.repository.employees.TransportPlannerRepository;
import com.tsl.repository.forwardingAndTransport.TruckRepository;
import com.tsl.service.calculators.SalaryBonusCalculator;
import com.tsl.service.forwardingAndTransport.TruckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TruckServiceTest {

    @Mock
    private TruckRepository truckRepository;
    @Mock
    private TruckMapper truckMapper;
    @Mock
    private TransportPlannerRepository transportPlannerRepository;
    @Mock
    private SalaryBonusCalculator salaryBonusCalculator;
    @InjectMocks
    private TruckService truckService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Trucks successfully")
    public void testFindAllTrucks_Success() {
        Truck truck1 = prepareTruck();
        Truck truck2 = prepareTruck();
        TruckDTO truckDTO1 = prepareTruckDTO();
        TruckDTO truckDTO2 = prepareTruckDTO();

        when(truckRepository.findAll()).thenReturn(Arrays.asList(truck1, truck2));
        when(truckMapper.mapToDTO(truck1)).thenReturn(truckDTO1);
        when(truckMapper.mapToDTO(truck2)).thenReturn(truckDTO2);

        List<TruckDTO> resultTrucks = truckService.findAllTrucks();

        assertNotNull(resultTrucks);
        assertEquals(2, resultTrucks.size());

        verify(truckRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Truck by ID successfully")
    public void testFindTruckById_Success() {
        Long truckId = 1L;
        Truck truck = prepareTruck();
        TruckDTO truckDTO = prepareTruckDTO();

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(truckMapper.mapToDTO(truck)).thenReturn(truckDTO);

        TruckDTO resultTruck = truckService.findTruckById(truckId);

        assertNotNull(resultTruck);
        assertEquals("ABC123/ABC123", resultTruck.getPlates());

        verify(truckRepository, times(1)).findById(truckId);
    }

    @Test
    @DisplayName("Should throw TruckNotFoundException when Truck is not found by ID")
    public void testFindTruckById_TruckNotFound() {
        Long truckId = 1L;

        when(truckRepository.findById(truckId)).thenReturn(Optional.empty());

        assertThrows(TruckNotFoundException.class, () -> truckService.findTruckById(truckId));

        verify(truckMapper, never()).mapToDTO(any());
    }

    @Test
    @DisplayName("Should add new Truck successfully and update transport planner salary bonus")
    public void testAddNewTruck_Success() {
        TruckDTO truckDTO = prepareTruckDTO();
        Truck truck = prepareTruck();
        TransportPlanner transportPlanner = prepareTransportPlanner();
        truckDTO.setTransportPlannerId(transportPlanner.getId());

        when(truckMapper.mapToEntity(truckDTO)).thenReturn(truck);
        when(truckRepository.save(truck)).thenReturn(truck);
        when(transportPlannerRepository.findById(truckDTO.getTransportPlannerId())).thenReturn(Optional.of(transportPlanner));
        when(salaryBonusCalculator.calculateSalaryBonusForPlanners(transportPlanner)).thenReturn(500.0);

        String result = truckService.addNewTruck(truckDTO);

        assertEquals("Truck added successfully!", result);
        assertEquals(500.0, transportPlanner.getSalaryBonus());

        verify(truckRepository, times(1)).save(truck);
        verify(transportPlannerRepository, times(1)).save(transportPlanner);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when updating Truck with non-existing transport planner ID")
    public void testUpdateTruck_ThrowsEmployeeNotFoundException() {
        TruckDTO truckDTO = prepareTruckDTO();
        Truck truck = prepareTruck();

        when(truckMapper.mapToEntity(truckDTO)).thenReturn(truck);
        when(transportPlannerRepository.findById(truckDTO.getTransportPlannerId())).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> truckService.updateTruck(truckDTO));

        verify(truckRepository, never()).save(truck);
        verify(transportPlannerRepository, never()).save(any());
    }

    private TruckDTO prepareTruckDTO() {
        TruckDTO truckDTO = new TruckDTO();
        truckDTO.setId(1L);
        truckDTO.setPlates("ABC123/ABC123");
        return truckDTO;
    }

    private Truck prepareTruck() {
        Truck truck = new Truck();
        truck.setId(1L);
        truck.setPlates("ABC123/ABC123");
        return truck;
    }

    private TransportPlanner prepareTransportPlanner() {
        TransportPlanner transportPlanner = new TransportPlanner();
        transportPlanner.setId(2L);
        transportPlanner.setSalaryBonus(0.0);
        return transportPlanner;
    }


}