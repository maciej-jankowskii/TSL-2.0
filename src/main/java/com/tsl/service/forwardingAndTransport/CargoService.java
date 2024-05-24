package com.tsl.service.forwardingAndTransport;

import com.tsl.dtos.forwardiing.CargoDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.CargoMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.model.employee.User;
import com.tsl.repository.forwardingAndTransport.CargoRepository;
import com.tsl.repository.forwardingAndTransport.CustomerRepository;
import com.tsl.repository.employees.UserRepository;
import com.tsl.service.calculators.VatCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CargoService {
    private final CargoRepository cargoRepository;
    private final CargoMapper cargoMapper;
    private final VatCalculatorService vatCalculatorService;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;


    /**
     * Finding methods
     */

    public List<CargoDTO> findAllCargos() {
        return cargoRepository.findAll().stream().map(cargoMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<CargoDTO> findAllCargosSortedBy(String sortBy) {
        return cargoRepository.findAllCargosBy(sortBy).stream().map(cargoMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<CargoDTO> findAllNotAssignedCargos() {
        return cargoRepository.findAllByAssignedToOrderIsFalse().stream().map(cargoMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<CargoDTO> findAllNotInvoicedCargos() {
        return cargoRepository.findAllByInvoicedFalse().stream().map(cargoMapper::mapToDTO).collect(Collectors.toList());
    }

    public CargoDTO findCargoById(Long id) {
        return cargoRepository.findById(id).map(cargoMapper::mapToDTO).orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
    }

    /**
     * Create, update, delete methods
     */

    @Transactional
    public CargoDTO addCargo(CargoDTO cargoDTO) {
        Cargo cargo = cargoMapper.mapToEntity(cargoDTO);
        Customer customer = customerRepository.findById(cargoDTO.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        addAdditionalDataForCargo(cargo, customer);
        checkingLoadingData(cargo);
        changeCustomerBalance(customer, cargo);

        customerRepository.save(customer);
        Cargo saved = cargoRepository.save(cargo);
        return cargoMapper.mapToDTO(saved);
    }


    @Transactional
    public void updateCargo(CargoDTO currentDTO, CargoDTO updatedDTO) {
        Cargo cargo = cargoMapper.mapToEntity(updatedDTO);
        checkingIsAssignedCargo(cargo);
        checkingUnauthorizedValueChange(currentDTO, updatedDTO);
        cargoRepository.save(cargo);
    }

    private static void checkingUnauthorizedValueChange(CargoDTO currentDTO, CargoDTO updatedDTO) {
        if (currentDTO.getInvoiced() == true && updatedDTO.getInvoiced() == false) {
            throw new CannotEditEntityException("Cannot change isInvoiced value from true to false");
        }
    }

    @Transactional
    public void deleteCargo(Long id) {
        Cargo cargo = cargoRepository.findById(id).orElseThrow(() -> new CargoNotFoundException("Cargo not found"));

        Customer customer = cargo.getCustomer();

        changeCustomerBalanceAfterDeleteCargo(cargo, customer);

        cargoRepository.deleteById(id);
    }

    /**
     * Helper methods
     */

    private void changeCustomerBalanceAfterDeleteCargo(Cargo cargo, Customer customer) {
        BigDecimal price = checkingGrossPrice(customer, cargo);
        BigDecimal balance = customer.getBalance();
        customer.setBalance(balance.subtract(price));
    }

    private static void checkingIsAssignedCargo(Cargo cargo) {
        if (cargo.getAssignedToOrder()) {
            throw new CannotEditEntityException("Cannot edit assigned cargo.");
        }
    }

    private void changeCustomerBalance(Customer customer, Cargo cargo) {
        BigDecimal price = checkingGrossPrice(customer, cargo);
        BigDecimal balance = customer.getBalance();
        customer.setBalance(balance.add(price));
    }

    private BigDecimal checkingGrossPrice(Customer customer, Cargo cargo) {
        String vatNumber = customer.getVatNumber();
        BigDecimal cargoPrice = cargo.getPrice();
        return vatCalculatorService.calculateGrossValue(cargoPrice, vatNumber);
    }

    private void addAdditionalDataForCargo(Cargo cargo, Customer customer) {
        User user = getLoggedInUser();
        cargo.setUser(user);
        cargo.setCustomer(customer);
        cargo.setDateAdded(LocalDate.now());
        cargo.setAssignedToOrder(false);
        cargo.setInvoiced(false);
        customer.getCargos().add(cargo);
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private static void checkingLoadingData(Cargo cargo) {
        LocalDate loadingDate = cargo.getLoadingDate();
        LocalDate unloadingDate = cargo.getUnloadingDate();
        if (unloadingDate != null && unloadingDate.isBefore(loadingDate)) {
            throw new WrongLoadigDateException("The loading date cannot be later than the unloading date");
        }
    }

}
