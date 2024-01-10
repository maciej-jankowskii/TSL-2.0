package com.tsl.service;

import com.tsl.dtos.ForwarderDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.ForwarderMapper;
import com.tsl.model.address.Address;
import com.tsl.model.employee.Forwarder;
import com.tsl.model.role.EmployeeRole;
import com.tsl.repository.AddressRepository;
import com.tsl.repository.EmployeeRoleRepository;
import com.tsl.repository.ForwarderRepository;
import com.tsl.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForwarderService {

    private final ForwarderRepository forwarderRepository;
    private final ForwarderMapper forwarderMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public ForwarderService(ForwarderRepository forwarderRepository, ForwarderMapper forwarderMapper, PasswordEncoder passwordEncoder, EmployeeRoleRepository employeeRoleRepository, UserRepository userRepository, AddressRepository addressRepository) {
        this.forwarderRepository = forwarderRepository;
        this.forwarderMapper = forwarderMapper;
        this.passwordEncoder = passwordEncoder;
        this.employeeRoleRepository = employeeRoleRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public List<ForwarderDTO> findAllForwarders(){
        return forwarderRepository.findAll().stream().map(forwarderMapper::mapToDTO).collect(Collectors.toList());
    }

    public ForwarderDTO findForwarderById(Long id) {
        return forwarderRepository.findById(id).map(forwarderMapper::mapToDTO).orElseThrow(() -> new EmployeeNotFoundException("Forwarder not found"));
    }

    @Transactional
    public String registerNewForwarder(ForwarderDTO forwarderDTO) throws RoleNotFoundException {
        checkingAvailabilityOfEmail(forwarderDTO);

        Forwarder forwarder = forwarderMapper.mapToEntity(forwarderDTO);

        Address address = addressRepository.findById(forwarderDTO.getAddressId()).orElseThrow(() -> new AddressNotFoundException("Address not found"));

        addAdditionalDataForForwarder(forwarderDTO, forwarder, address);

        forwarderRepository.save(forwarder);
        return "User registered successfully!";
    }

    @Transactional
    public void updateForwarder(ForwarderDTO forwarderDTO) {
        Forwarder forwarder = forwarderMapper.mapToEntity(forwarderDTO);
        forwarderRepository.save(forwarder);
    }

    public void deleteForwarder(Long id) {
        forwarderRepository.deleteById(id);
    }

    private void checkingAvailabilityOfEmail(ForwarderDTO forwarderDTO) {
        if (userRepository.existsByEmail(forwarderDTO.getEmail())){
            throw new EmailAddressIsTaken("Email address is already taken");
        }
    }


    private void addAdditionalDataForForwarder(ForwarderDTO forwarderDTO, Forwarder forwarder, Address address) throws RoleNotFoundException {
        forwarder.setTotalMargin(BigDecimal.valueOf(0.0));
        forwarder.setSalaryBonus(0.0);
        forwarder.setPassword(passwordEncoder.encode(forwarderDTO.getPassword()));
        EmployeeRole role = employeeRoleRepository.findByName("FORWARDER").orElseThrow(() -> new RoleNotFoundException("Role not found"));
        forwarder.setRoles(Collections.singletonList(role));
        forwarder.setAddress(address);
    }
}
