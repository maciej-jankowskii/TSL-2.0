package com.tsl.service;

import com.tsl.dtos.CargoDTO;
import com.tsl.exceptions.CustomerNotFoundException;
import com.tsl.exceptions.WrongLoadigDateException;
import com.tsl.mapper.CargoMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.model.employee.User;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.CustomerRepository;
import com.tsl.repository.UserRepository;
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
public class CargoService {
    private final CargoRepository cargoRepository;
    private final CargoMapper cargoMapper;
    private final VatCalculatorService vatCalculatorService;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CargoService(CargoRepository cargoRepository, CargoMapper cargoMapper,
                        VatCalculatorService vatCalculatorService, CustomerRepository customerRepository, UserRepository userRepository) {
        this.cargoRepository = cargoRepository;
        this.cargoMapper = cargoMapper;
        this.vatCalculatorService = vatCalculatorService;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public List<CargoDTO> findAllCargos(){
        return cargoRepository.findAll().stream().map(cargoMapper::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public CargoDTO addCargo(CargoDTO cargoDTO){
        Customer customer = extractCustomerFromCargoDTO(cargoDTO);
        Cargo cargo = cargoMapper.mapToEntity(cargoDTO);

        addAdditionalDataForCargo(cargo, customer);
        checkingLoadingData(cargo);
        changeCustomerBalance(customer, cargo);

        customerRepository.save(customer);
        Cargo saved = cargoRepository.save(cargo);
        return cargoMapper.mapToDTO(saved);
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

    private  User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private static void checkingLoadingData(Cargo cargo) {
        LocalDate loadingDate = cargo.getLoadingDate();
        LocalDate unloadingDate = cargo.getUnloadingDate();
        if (unloadingDate != null && unloadingDate.isBefore(loadingDate)){
            throw new WrongLoadigDateException("The loading date cannot be later than the unloading date");
        }
    }

    private Customer extractCustomerFromCargoDTO(CargoDTO cargoDTO) {
        Long customerId = cargoDTO.getCustomerId();
        return customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }


}
