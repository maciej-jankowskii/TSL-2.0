package com.tsl.service;

import com.tsl.dtos.TruckDTO;
import com.tsl.mapper.TruckMapper;
import com.tsl.repository.TruckRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TruckService {
    private final TruckRepository truckRepository;
    private final TruckMapper truckMapper;

    public TruckService(TruckRepository truckRepository, TruckMapper truckMapper) {
        this.truckRepository = truckRepository;
        this.truckMapper = truckMapper;
    }

    public List<TruckDTO> findAllTrucks(){
        return truckRepository.findAll().stream().map(truckMapper::mapToDTO).collect(Collectors.toList());
    }
    public List<TruckDTO> findAllTruckSortedBy(String sortBy){
        return truckRepository.findAllTrucksBy(sortBy).stream().map(truckMapper::mapToDTO).collect(Collectors.toList());
    }
}
