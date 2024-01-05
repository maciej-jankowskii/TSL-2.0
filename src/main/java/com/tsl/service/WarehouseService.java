package com.tsl.service;

import com.tsl.dtos.WarehouseDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.CannotDeleteEntityException;
import com.tsl.exceptions.WarehouseNotFoundException;
import com.tsl.mapper.WarehouseMapper;
import com.tsl.model.address.Address;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AddressRepository addressRepository;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper, AddressRepository addressRepository) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.addressRepository = addressRepository;
    }

    /**
     Finding methods
     */

    public List<WarehouseDTO> findAllWarehouses() {
        return warehouseRepository.findAll().stream().map(warehouseMapper::mapToDTO).collect(Collectors.toList());
    }

    public WarehouseDTO findWarehouseById(Long id) {
        return warehouseRepository.findById(id).map(warehouseMapper::mapToDTO).orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
    }

    public List<WarehouseDTO> findAllWarehousesSortedBy(String sortBy) {
        return warehouseRepository.findAllOrderBy(sortBy).stream().map(warehouseMapper::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Create, update, delete methods
     */

    @Transactional
    public WarehouseDTO addWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseMapper.mapToEntity(warehouseDTO);

        Address address = addressRepository.findById(warehouseDTO.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));
        warehouse.setAddress(address);

        Warehouse saved = warehouseRepository.save(warehouse);
        return warehouseMapper.mapToDTO(saved);
    }

    @Transactional
    public void updateWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseMapper.mapToEntity(warehouseDTO);
        warehouseRepository.save(warehouse);
    }

    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
        List<WarehouseOrder> list = warehouse.getWarehouseOrders().stream().filter(order -> !order.getIsCompleted()).collect(Collectors.toList());

        checkingNotCompletedWarehouseOrders(list);
        checkingWarehouseEmployees(warehouse);

        warehouseRepository.deleteById(id);
    }

    /**
     * Helper methods
     */

    private static void checkingWarehouseEmployees(Warehouse warehouse) {
        if (!warehouse.getWarehouseWorkers().isEmpty()) {
            throw new CannotDeleteEntityException("Cannot delete Warehouse because warehouse has employees");
        }
    }

    private static void checkingNotCompletedWarehouseOrders(List<WarehouseOrder> list) {
        if (!list.isEmpty()) {
            throw new CannotDeleteEntityException("Cannot delete warehouse because warehouse has not completed orders");
        }
    }
}
