package com.tsl.mapper;

import com.tsl.dtos.CargoDTO;
import com.tsl.enums.Currency;
import com.tsl.exceptions.CustomerNotFoundException;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.repository.CustomerRepository;
import com.tsl.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CargoMapper {

    private final CustomerRepository customerRepository;

    public CargoMapper(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Cargo mapToEntity(CargoDTO cargoDTO) {
        if (cargoDTO == null) {
            throw new NullEntityException("Cargo data cannot be null");
        }
        Cargo cargo = new Cargo();
        cargo.setId(cargoDTO.getId());
        cargo.setCargoNumber(cargoDTO.getCargoNumber());
        cargo.setPrice(cargoDTO.getPrice());
        cargo.setCurrency(Currency.valueOf(cargoDTO.getCurrency()));
        cargo.setDateAdded(cargoDTO.getDateAdded());
        cargo.setLoadingDate(cargoDTO.getLoadingDate());
        cargo.setUnloadingDate(cargoDTO.getUnloadingDate());
        cargo.setLoadingAddress(cargoDTO.getLoadingAddress());
        cargo.setUnloadingAddress(cargoDTO.getUnloadingAddress());
        cargo.setGoods(cargoDTO.getGoods());
        cargo.setDescription(cargoDTO.getDescription());
        cargo.setAssignedToOrder(cargoDTO.getAssignedToOrder());
        cargo.setInvoiced(cargoDTO.getInvoiced());
        return cargo;
    }

    public CargoDTO mapToDTO(Cargo cargo) {
        if (cargo == null) {
            throw new NullEntityException("Cargo cannot be null");
        }

        CargoDTO dto = new CargoDTO();
        dto.setId(cargo.getId());
        dto.setCargoNumber(cargo.getCargoNumber());
        dto.setPrice(cargo.getPrice());
        dto.setCurrency(String.valueOf(cargo.getCurrency()));
        dto.setDateAdded(cargo.getDateAdded());
        dto.setLoadingDate(cargo.getLoadingDate());
        dto.setUnloadingDate(cargo.getUnloadingDate());
        dto.setLoadingAddress(cargo.getLoadingAddress());
        dto.setUnloadingAddress(cargo.getUnloadingAddress());
        dto.setGoods(cargo.getGoods());
        dto.setDescription(cargo.getDescription());
        dto.setAssignedToOrder(cargo.getAssignedToOrder());
        dto.setInvoiced(cargo.getInvoiced());
        dto.setCustomerId(cargo.getCustomer().getId());
        dto.setCreatedById(cargo.getUser().getId());
        return dto;
    }
}
