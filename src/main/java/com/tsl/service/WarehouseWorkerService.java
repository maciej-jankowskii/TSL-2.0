package com.tsl.service;

import com.tsl.dtos.TransportPlannerDTO;
import com.tsl.dtos.WarehouseWorkerDTO;
import com.tsl.exceptions.PlannerNotFoundException;
import com.tsl.exceptions.WarehouseWorkerNotFoundException;
import com.tsl.mapper.WarehouseWorkerMapper;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.employee.WarehouseWorker;
import com.tsl.repository.WarehouseWorkerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseWorkerService {
    private final WarehouseWorkerRepository warehouseWorkerRepository;
    private final WarehouseWorkerMapper warehouseWorkerMapper;
    private final PasswordEncoder passwordEncoder;

    public WarehouseWorkerService(WarehouseWorkerRepository warehouseWorkerRepository,
                                  WarehouseWorkerMapper warehouseWorkerMapper, PasswordEncoder passwordEncoder) {
        this.warehouseWorkerRepository = warehouseWorkerRepository;
        this.warehouseWorkerMapper = warehouseWorkerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<WarehouseWorkerDTO> findAllWarehouseWorkers(){
        return warehouseWorkerRepository.findAll().stream().map(warehouseWorkerMapper::mapToDTO).collect(Collectors.toList());
    }

    public WarehouseWorkerDTO findWarehouseWorkerById(Long id) {
        return warehouseWorkerRepository.findById(id).map(warehouseWorkerMapper::mapToDTO).orElseThrow(() -> new WarehouseWorkerNotFoundException("Warehouse worker not found"));
    }

    @Transactional
    public void updateWarehouseWorker(WarehouseWorkerDTO warehouseWorkerDTO) {
        WarehouseWorker worker = warehouseWorkerMapper.mapToEntity(warehouseWorkerDTO);
        warehouseWorkerRepository.save(worker);
    }

    public void deleteWarehouseWorker(Long id) {
        warehouseWorkerRepository.deleteById(id);
    }

    @Transactional
    public String registerNewWarehouseWorker(WarehouseWorkerDTO dto){
        WarehouseWorker worker = warehouseWorkerMapper.mapToEntity(dto);

        warehouseWorkerRepository.save(worker);
        return "User registered successfully!";
    }
}
