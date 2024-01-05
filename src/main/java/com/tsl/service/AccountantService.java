package com.tsl.service;

import com.tsl.dtos.AccountantDTO;
import com.tsl.exceptions.EmailAddressIsTaken;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.mapper.AccountantMapper;
import com.tsl.model.employee.Accountant;
import com.tsl.model.role.EmployeeRole;
import com.tsl.repository.AccountantRepository;
import com.tsl.repository.EmployeeRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountantService {
    private final AccountantMapper accountantMapper;
    private final AccountantRepository accountantRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRoleRepository employeeRoleRepository;

    public AccountantService(AccountantMapper accountantMapper, AccountantRepository accountantRepository, PasswordEncoder passwordEncoder, EmployeeRoleRepository employeeRoleRepository) {
        this.accountantMapper = accountantMapper;
        this.accountantRepository = accountantRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeRoleRepository = employeeRoleRepository;
    }

    public List<AccountantDTO> findAllAccountants(){
        return accountantRepository.findAll().stream().map(accountantMapper::mapToDTO).collect(Collectors.toList());
    }

    public AccountantDTO findAccountantById(Long id) {
        return accountantRepository.findById(id).map(accountantMapper::mapToDTO).orElseThrow(() -> new EmployeeNotFoundException("Accountant not found"));
    }

    @Transactional
    public void updateAccountant(AccountantDTO accountantDTO) {
        Accountant accountant = accountantMapper.mapToEntity(accountantDTO);
        accountantRepository.save(accountant);
    }

    public void deleteAccountant(Long id) {
        accountantRepository.deleteById(id);
    }
    @Transactional
    public String registerNewAccountant(AccountantDTO accountantDTO) throws RoleNotFoundException {
        checkingAvailabilityOfEmail(accountantDTO);

        Accountant accountant = accountantMapper.mapToEntity(accountantDTO);
        addAdditionalDataForAccountant(accountantDTO, accountant);

        accountantRepository.save(accountant);
        return "User registered successfully!";
    }

    private void addAdditionalDataForAccountant(AccountantDTO accountantDTO, Accountant accountant) throws RoleNotFoundException {
        accountant.setPassword(passwordEncoder.encode(accountantDTO.getPassword()));
        EmployeeRole role = employeeRoleRepository.findByName("ACCOUNTANT").orElseThrow(() -> new RoleNotFoundException("Role not found"));
        accountant.setRoles(Collections.singletonList(role));
    }

    private void checkingAvailabilityOfEmail(AccountantDTO accountantDTO) {
        if (accountantRepository.existsByEmail(accountantDTO.getEmail())){
            throw new EmailAddressIsTaken("Email address is already taken");
        }
    }
}
