package com.tsl.service;

import com.tsl.dtos.employees.WarehouseWorkerDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.exceptions.WarehouseNotFoundException;
import com.tsl.mapper.WarehouseWorkerMapper;
import com.tsl.model.address.Address;
import com.tsl.model.employee.WarehouseWorker;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.repository.contactAndAddress.AddressRepository;
import com.tsl.repository.warehouses.WarehouseRepository;
import com.tsl.repository.employees.WarehouseWorkerRepository;
import com.tsl.service.employees.WarehouseWorkerService;
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

class WarehouseWorkerServiceTest {

    @Mock
    private WarehouseWorkerRepository warehouseWorkerRepository;
    @Mock
    private WarehouseWorkerMapper warehouseWorkerMapper;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseWorkerService warehouseWorkerService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Warehouse Workers successfully")
    public void testFindAllWarehouseWorkers_Success() {
        WarehouseWorker worker1 = prepareWarehouseWorker();
        WarehouseWorker worker2 = prepareWarehouseWorker();
        WarehouseWorkerDTO workerDTO1 = prepareWarehouseWorkerDTO();
        WarehouseWorkerDTO workerDTO2 = prepareWarehouseWorkerDTO();

        when(warehouseWorkerRepository.findAll()).thenReturn(Arrays.asList(worker1, worker2));
        when(warehouseWorkerMapper.mapToDTO(worker1)).thenReturn(workerDTO1);
        when(warehouseWorkerMapper.mapToDTO(worker2)).thenReturn(workerDTO2);

        List<WarehouseWorkerDTO> resultWorkers = warehouseWorkerService.findAllWarehouseWorkers();

        assertNotNull(resultWorkers);
        assertEquals(2, resultWorkers.size());

        verify(warehouseWorkerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Warehouse Worker by ID successfully")
    public void testFindWarehouseWorkerById_Success() {
        Long workerId = 1L;
        WarehouseWorker worker = prepareWarehouseWorker();
        WarehouseWorkerDTO workerDTO = prepareWarehouseWorkerDTO();

        when(warehouseWorkerRepository.findById(workerId)).thenReturn(Optional.of(worker));
        when(warehouseWorkerMapper.mapToDTO(worker)).thenReturn(workerDTO);

        WarehouseWorkerDTO resultWorker = warehouseWorkerService.findWarehouseWorkerById(workerId);

        assertNotNull(resultWorker);
        assertEquals("John", resultWorker.getFirstName());

        verify(warehouseWorkerRepository, times(1)).findById(workerId);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when Warehouse Worker is not found by ID")
    public void testFindWarehouseWorkerById_WorkerNotFound() {
        Long workerId = 1L;

        when(warehouseWorkerRepository.findById(workerId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> warehouseWorkerService.findWarehouseWorkerById(workerId));

        verify(warehouseWorkerMapper, never()).mapToDTO(any());
    }

    @Test
    @DisplayName("Should register new Warehouse Worker successfully")
    public void testRegisterNewWarehouseWorker_Success() {
        WarehouseWorkerDTO workerDTO = prepareWarehouseWorkerDTO();
        WarehouseWorker worker = prepareWarehouseWorker();
        Address address = prepareAddress();
        Warehouse warehouse = prepareWarehouse();

        when(warehouseWorkerMapper.mapToEntity(workerDTO)).thenReturn(worker);
        when(addressRepository.findById(workerDTO.getAddressId())).thenReturn(Optional.of(address));
        when(warehouseRepository.findById(workerDTO.getWarehouseId())).thenReturn(Optional.of(warehouse));
        when(warehouseWorkerRepository.save(worker)).thenReturn(worker);

        String result = warehouseWorkerService.registerNewWarehouseWorker(workerDTO);

        assertNotNull(result);
        assertEquals("User registered successfully!", result);

        verify(warehouseWorkerRepository, times(1)).save(worker);
    }

    @Test
    @DisplayName("Should throw AddressNotFoundException when Address is not found during Warehouse Worker registration")
    public void testRegisterNewWarehouseWorker_AddressNotFound() {
        WarehouseWorkerDTO workerDTO = prepareWarehouseWorkerDTO();
        WarehouseWorker worker = prepareWarehouseWorker();

        when(warehouseWorkerMapper.mapToEntity(workerDTO)).thenReturn(worker);
        when(addressRepository.findById(workerDTO.getAddressId())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> warehouseWorkerService.registerNewWarehouseWorker(workerDTO));

        verify(warehouseRepository, never()).findById(any());
        verify(warehouseWorkerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw WarehouseNotFoundException when Warehouse is not found during Warehouse Worker registration")
    public void testRegisterNewWarehouseWorker_WarehouseNotFound() {
        WarehouseWorkerDTO workerDTO = prepareWarehouseWorkerDTO();
        WarehouseWorker worker = prepareWarehouseWorker();
        Address address = prepareAddress();

        when(warehouseWorkerMapper.mapToEntity(workerDTO)).thenReturn(worker);
        when(addressRepository.findById(workerDTO.getAddressId())).thenReturn(Optional.of(address));
        when(warehouseRepository.findById(workerDTO.getWarehouseId())).thenReturn(Optional.empty());


        assertThrows(WarehouseNotFoundException.class, () -> warehouseWorkerService.registerNewWarehouseWorker(workerDTO));

        verify(warehouseWorkerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Warehouse Worker successfully")
    public void testUpdateWarehouseWorker_Success() {
        WarehouseWorkerDTO workerDTO = prepareWarehouseWorkerDTO();
        WarehouseWorker worker = prepareWarehouseWorker();

        when(warehouseWorkerMapper.mapToEntity(workerDTO)).thenReturn(worker);
        when(warehouseWorkerRepository.save(worker)).thenReturn(worker);

        assertDoesNotThrow(() -> warehouseWorkerService.updateWarehouseWorker(workerDTO));

        verify(warehouseWorkerRepository, times(1)).save(worker);
    }

    private WarehouseWorkerDTO prepareWarehouseWorkerDTO() {
        WarehouseWorkerDTO workerDTO = new WarehouseWorkerDTO();
        workerDTO.setId(1L);
        workerDTO.setFirstName("John");
        workerDTO.setLastName("Doe");
        workerDTO.setAddressId(2L);
        workerDTO.setWarehouseId(3L);
        return workerDTO;
    }

    private WarehouseWorker prepareWarehouseWorker() {
        WarehouseWorker worker = new WarehouseWorker();
        worker.setId(1L);
        worker.setFirstName("John");
        worker.setLastName("Doe");
        return worker;
    }

    private Address prepareAddress() {
        Address address = new Address();
        address.setId(2L);
        address.setCity("City");
        address.setStreet("Street");
        return address;
    }

    private Warehouse prepareWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(3L);
        return warehouse;
    }

}