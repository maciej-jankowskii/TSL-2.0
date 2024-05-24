package com.tsl.service.employees;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseWorkerService {
    private final WarehouseWorkerRepository warehouseWorkerRepository;
    private final AddressRepository addressRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseWorkerMapper warehouseWorkerMapper;


    public List<WarehouseWorkerDTO> findAllWarehouseWorkers() {
        return warehouseWorkerRepository.findAll().stream().map(warehouseWorkerMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public WarehouseWorkerDTO findWarehouseWorkerById(Long id) {
        return warehouseWorkerRepository.findById(id).map(warehouseWorkerMapper::mapToDTO)
                .orElseThrow(() -> new EmployeeNotFoundException("Warehouse worker not found"));
    }

    @Transactional
    public String registerNewWarehouseWorker(WarehouseWorkerDTO dto) {
        WarehouseWorker worker = warehouseWorkerMapper.mapToEntity(dto);

        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        worker.setAddress(address);

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
        worker.setWarehouse(warehouse);


        warehouseWorkerRepository.save(worker);
        return "User registered successfully!";
    }

    @Transactional
    public void updateWarehouseWorker(WarehouseWorkerDTO warehouseWorkerDTO) {
        WarehouseWorker worker = warehouseWorkerMapper.mapToEntity(warehouseWorkerDTO);
        warehouseWorkerRepository.save(worker);
    }

    public void deleteWarehouseWorker(Long id) {
        warehouseWorkerRepository.deleteById(id);
    }

}
