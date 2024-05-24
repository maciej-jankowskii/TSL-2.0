package com.tsl.service.employees;

import com.tsl.dtos.employees.AccountantDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.EmailAddressIsTaken;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.mapper.AccountantMapper;
import com.tsl.model.address.Address;
import com.tsl.model.employee.Accountant;
import com.tsl.model.role.EmployeeRole;
import com.tsl.repository.employees.AccountantRepository;
import com.tsl.repository.contactAndAddress.AddressRepository;
import com.tsl.repository.employees.EmployeeRoleRepository;
import com.tsl.repository.employees.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountantService {
    private final AccountantMapper accountantMapper;
    private final AccountantRepository accountantRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;


    public List<AccountantDTO> findAllAccountants() {
        return accountantRepository.findAll().stream().map(accountantMapper::mapToDTO).collect(Collectors.toList());
    }

    public AccountantDTO findAccountantById(Long id) {
        return accountantRepository.findById(id).map(accountantMapper::mapToDTO).orElseThrow(() -> new EmployeeNotFoundException("Accountant not found"));
    }

    @Transactional
    public String registerNewAccountant(AccountantDTO accountantDTO) throws RoleNotFoundException {
        checkingAvailabilityOfEmail(accountantDTO);

        Accountant accountant = accountantMapper.mapToEntity(accountantDTO);
        Address address = addressRepository.findById(accountantDTO.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));
        accountant.setAddress(address);

        addAdditionalDataForAccountant(accountantDTO, accountant);

        accountantRepository.save(accountant);
        return "User registered successfully!";
    }

    @Transactional
    public void updateAccountant(AccountantDTO accountantDTO) {
        Accountant accountant = accountantMapper.mapToEntity(accountantDTO);
        accountantRepository.save(accountant);
    }

    public void deleteAccountant(Long id) {
        accountantRepository.deleteById(id);
    }

    private void addAdditionalDataForAccountant(AccountantDTO accountantDTO, Accountant accountant) throws RoleNotFoundException {
        accountant.setPassword(passwordEncoder.encode(accountantDTO.getPassword()));
        EmployeeRole role = employeeRoleRepository.findByName("ACCOUNTANT").orElseThrow(() -> new RoleNotFoundException("Role not found"));
        accountant.setRoles(Collections.singletonList(role));
    }

    private void checkingAvailabilityOfEmail(AccountantDTO accountantDTO) {
        if (userRepository.existsByEmail(accountantDTO.getEmail())) {
            throw new EmailAddressIsTaken("Email address is already taken");
        }
    }
}
