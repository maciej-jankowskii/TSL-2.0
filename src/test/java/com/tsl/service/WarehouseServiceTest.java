package com.tsl.service;

import com.tsl.dtos.warehouses.WarehouseDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.CannotDeleteEntityException;
import com.tsl.exceptions.WarehouseNotFoundException;
import com.tsl.mapper.WarehouseMapper;
import com.tsl.model.address.Address;
import com.tsl.model.employee.WarehouseWorker;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.contactAndAddress.AddressRepository;
import com.tsl.repository.warehouses.WarehouseRepository;
import com.tsl.service.warehouses.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private WarehouseMapper warehouseMapper;
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private WarehouseService warehouseService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Warehouses")
    public void testFindAllWarehouses_Success() {
        Warehouse warehouse1 = prepareFirstWarehouse();
        Warehouse warehouse2 = prepareSecondWarehouse();
        WarehouseDTO warehouseDTO1 = prepareFirstWarehouseDTO();
        WarehouseDTO warehouseDTO2 = prepareSecondWarehouseDTO();
        List<Warehouse> warehouses = Arrays.asList(warehouse1, warehouse2);

        when(warehouseRepository.findAll()).thenReturn(warehouses);
        when(warehouseMapper.mapToDTO(warehouse1)).thenReturn(warehouseDTO1);
        when(warehouseMapper.mapToDTO(warehouse2)).thenReturn(warehouseDTO2);
        List<WarehouseDTO> resultWarehouses = warehouseService.findAllWarehouses();

        assertNotNull(resultWarehouses);
        assertEquals(resultWarehouses.size(), warehouses.size());
        verify(warehouseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Warehouse by ID")
    public void testFindWarehouseById_Success() {
        Warehouse warehouse = prepareFirstWarehouse();
        WarehouseDTO warehouseDTO = prepareFirstWarehouseDTO();

        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
        when(warehouseMapper.mapToDTO(warehouse)).thenReturn(warehouseDTO);

        WarehouseDTO resultWarehouse = warehouseService.findWarehouseById(warehouse.getId());

        assertEquals(resultWarehouse, warehouseDTO);
        verify(warehouseRepository, times(1)).findById(warehouse.getId());
    }

    @Test
    @DisplayName("Should throw WarehouseNotFoundException")
    public void testFindWarehouseById_WarehouseNotFound() {
        Long warehouseId = 1L;

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.findWarehouseById(warehouseId));
    }

    @Test
    @DisplayName("Should add new Warehouse")
    public void testAddWarehouse_Success() {
        Warehouse warehouse = prepareFirstWarehouse();
        WarehouseDTO warehouseDTO = prepareFirstWarehouseDTO();
        Address address = prepareAddress();

        when(warehouseMapper.mapToEntity(warehouseDTO)).thenReturn(warehouse);
        when(addressRepository.findById(warehouseDTO.getAddressId())).thenReturn(Optional.of(address));
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        when(warehouseMapper.mapToDTO(warehouse)).thenReturn(warehouseDTO);

        WarehouseDTO result = warehouseService.addWarehouse(warehouseDTO);

        verify(warehouseRepository, times(1)).save(any());
        assertEquals(1, result.getId());
    }

    @Test
    @DisplayName("Should throw AddressNotFoundException when adding Warehouse with non-existent address ID")
    public void testAddWarehouse_NonExistentAddress() {
        WarehouseDTO warehouseDTO = prepareFirstWarehouseDTO();

        when(addressRepository.findById(warehouseDTO.getAddressId())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> warehouseService.addWarehouse(warehouseDTO));

        verify(warehouseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete Warehouse successfully")
    public void testDeleteWarehouse_Success() {
        Warehouse warehouse = prepareFirstWarehouse();
        List<WarehouseOrder> warehouseOrders = Collections.emptyList();

        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        assertDoesNotThrow(() -> warehouseService.deleteWarehouse(warehouse.getId()));

        verify(warehouseRepository, times(1)).deleteById(warehouse.getId());
    }

    @Test
    @DisplayName("Should throw WarehouseNotFoundException when deleting non-existent Warehouse")
    public void testDeleteWarehouse_WarehouseNotFound() {
        Long warehouseId = 1L;

        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.deleteWarehouse(warehouseId));

        verify(warehouseRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw CannotDeleteEntityException when deleting Warehouse with not completed orders")
    public void testDeleteWarehouse_NotCompletedOrders() {
        Warehouse warehouse = prepareFirstWarehouse();
        WarehouseOrder order = new WarehouseOrder();
        order.setIsCompleted(false);
        warehouse.setWarehouseOrders(Collections.singletonList(order));

        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));

        assertThrows(CannotDeleteEntityException.class, () -> warehouseService.deleteWarehouse(warehouse.getId()));

        verify(warehouseRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw CannotDeleteEntityException when deleting Warehouse with employees")
    public void testDeleteWarehouse_EmployeesExist() {
        Warehouse warehouse = prepareFirstWarehouse();
        warehouse.getWarehouseWorkers().add(new WarehouseWorker());

        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));

        assertThrows(CannotDeleteEntityException.class, () -> warehouseService.deleteWarehouse(warehouse.getId()));

        verify(warehouseRepository, never()).deleteById(any());
    }

    private Address prepareAddress() {
        Address address = new Address();
        address.setId(1L);
        address.setStreet("Main Street");
        return address;
    }

    private WarehouseDTO prepareFirstWarehouseDTO() {
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(1L);
        return warehouseDTO;
    }

    private WarehouseDTO prepareSecondWarehouseDTO() {
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(2L);
        return warehouseDTO;
    }

    private static Warehouse prepareSecondWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(2L);
        return warehouse;
    }

    private static Warehouse prepareFirstWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        return warehouse;
    }


}