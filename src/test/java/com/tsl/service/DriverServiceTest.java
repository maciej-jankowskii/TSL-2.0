package com.tsl.service;

import com.tsl.dtos.DetailedDriverDTO;
import com.tsl.exceptions.DriverIsAlreadyAssignedToTruck;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.exceptions.TruckNotFoundException;
import com.tsl.mapper.DetailedDriverMapper;
import com.tsl.model.employee.Driver;
import com.tsl.model.truck.Truck;
import com.tsl.repository.DriverRepository;
import com.tsl.repository.TruckRepository;
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
import static org.mockito.Mockito.*;

class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;
    @Mock
    private DetailedDriverMapper detailedDriverMapper;
    @Mock private TruckRepository truckRepository;
    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Drivers with all info successfully")
    public void testFindAllDriversWithAllInfo_Success() {
        Driver driver1 = prepareDriver();
        Driver driver2 = prepareDriver();
        DetailedDriverDTO detailedDriverDTO1 = prepareDetailedDriverDTO();
        DetailedDriverDTO detailedDriverDTO2 = prepareDetailedDriverDTO();

        when(driverRepository.findAll()).thenReturn(Arrays.asList(driver1, driver2));
        when(detailedDriverMapper.mapToDTO(driver1)).thenReturn(detailedDriverDTO1);
        when(detailedDriverMapper.mapToDTO(driver2)).thenReturn(detailedDriverDTO2);

        List<DetailedDriverDTO> resultDrivers = driverService.findAllDriversWithAllInfo();

        assertNotNull(resultDrivers);
        assertEquals(2, resultDrivers.size());

        verify(driverRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Driver by ID with all info successfully")
    public void testFindDriverByIdWithAllInfo_Success() {
        Long driverId = 1L;
        Driver driver = prepareDriver();
        DetailedDriverDTO detailedDriverDTO = prepareDetailedDriverDTO();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(detailedDriverMapper.mapToDTO(driver)).thenReturn(detailedDriverDTO);

        DetailedDriverDTO resultDriver = driverService.findDriverById(driverId);

        assertNotNull(resultDriver);
        assertEquals("John Doe", resultDriver.getFirstName());

        verify(driverRepository, times(1)).findById(driverId);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when Driver is not found by ID")
    public void testFindDriverByIdWithAllInfo_DriverNotFound() {
        Long driverId = 1L;

        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> driverService.findDriverById(driverId));

        verify(detailedDriverMapper, never()).mapToDTO(any());
    }

    @Test
    @DisplayName("Should register new Driver successfully")
    public void testRegisterNewDriver_Success() {
        DetailedDriverDTO detailedDriverDTO = prepareDetailedDriverDTO();
        Driver driver = prepareDriver();

        when(detailedDriverMapper.mapToEntity(detailedDriverDTO)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);

        String result = driverService.registerNewDriver(detailedDriverDTO);

        assertEquals("User registered successfully!", result);
        verify(driverRepository, times(1)).save(driver);
    }

    @Test
    @DisplayName("Should assign Truck to Driver successfully")
    public void testAssignTruckToDriver_Success() {
        Long driverId = 1L;
        Long truckId = 2L;
        Driver driver = prepareDriver();
        Truck truck = prepareTruck();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));

        assertDoesNotThrow(() -> driverService.assignTruckToDriver(driverId, truckId));

        assertTrue(driver.getAssignedToTruck());
        assertTrue(driver.getMainDriver());
        assertTrue(truck.getAssignedToDriver());

        verify(driverRepository, times(1)).save(driver);
        verify(truckRepository, times(1)).save(truck);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when Driver is not found")
    public void testAssignTruckToDriver_DriverNotFound() {
        Long driverId = 1L;
        Long truckId = 2L;

        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> driverService.assignTruckToDriver(driverId, truckId));

        verify(truckRepository, never()).findById(truckId);
        verify(driverRepository, never()).save(any());
        verify(truckRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw TruckNotFoundException when Truck is not found")
    public void testAssignTruckToDriver_TruckNotFound() {
        Long driverId = 1L;
        Long truckId = 2L;
        Driver driver = prepareDriver();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(truckRepository.findById(truckId)).thenReturn(Optional.empty());

        assertThrows(TruckNotFoundException.class, () -> driverService.assignTruckToDriver(driverId, truckId));

        verify(driverRepository, never()).save(any());
        verify(truckRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DriverIsAlreadyAssignedToTruck when Driver is already assigned to another Truck")
    public void testAssignTruckToDriver_DriverAlreadyAssigned() {
        Long driverId = 1L;
        Long truckId = 2L;
        Driver driver = prepareAssignedDriver();
        Truck truck = prepareTruck();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));

        assertThrows(DriverIsAlreadyAssignedToTruck.class, () -> driverService.assignTruckToDriver(driverId, truckId));

        verify(driverRepository, never()).save(any());
        verify(truckRepository, never()).save(any());
    }

    private Driver prepareAssignedDriver() {
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setFirstName("John Doe");
        driver.setAssignedToTruck(true);
        return driver;
    }

    private Truck prepareTruck() {
        Truck truck = new Truck();
        truck.setId(2L);
        truck.setPlates("ABC123/ABC123");
        return truck;
    }

    private DetailedDriverDTO prepareDetailedDriverDTO() {
        DetailedDriverDTO detailedDriverDTO = new DetailedDriverDTO();
        detailedDriverDTO.setId(1L);
        detailedDriverDTO.setFirstName("John Doe");
        detailedDriverDTO.setDriverLicenceNumber("ABC123");
        return detailedDriverDTO;
    }

    private Driver prepareDriver() {
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setFirstName("John Doe");
        driver.setDriverLicenceNumber("ABC123");
        driver.setAssignedToTruck(false);
        return driver;
    }

}