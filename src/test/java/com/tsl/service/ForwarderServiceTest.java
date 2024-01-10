package com.tsl.service;

import com.tsl.dtos.employees.ForwarderDTO;
import com.tsl.exceptions.EmailAddressIsTaken;
import com.tsl.mapper.ForwarderMapper;
import com.tsl.model.address.Address;
import com.tsl.model.employee.Forwarder;
import com.tsl.model.role.EmployeeRole;
import com.tsl.repository.contactAndAddress.AddressRepository;
import com.tsl.repository.employees.EmployeeRoleRepository;
import com.tsl.repository.employees.ForwarderRepository;
import com.tsl.repository.employees.UserRepository;
import com.tsl.service.employees.ForwarderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForwarderServiceTest {
    @Mock
    private ForwarderRepository forwarderRepository;
    @Mock
    private ForwarderMapper forwarderMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmployeeRoleRepository employeeRoleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;
    @InjectMocks
    private ForwarderService forwarderService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return a list of all forwarders")
    void testFindAllForwarders() {
        Forwarder forwarder1 = new Forwarder();
        Forwarder forwarder2 = new Forwarder();
        List<Forwarder> forwarderList = Arrays.asList(forwarder1, forwarder2);

        ForwarderDTO forwarderDTO1 = new ForwarderDTO();
        ForwarderDTO forwarderDTO2 = new ForwarderDTO();
        when(forwarderRepository.findAll()).thenReturn(forwarderList);
        when(forwarderMapper.mapToDTO(forwarder1)).thenReturn(forwarderDTO1);
        when(forwarderMapper.mapToDTO(forwarder2)).thenReturn(forwarderDTO2);

        List<ForwarderDTO> result = forwarderService.findAllForwarders();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(forwarderDTO1, result.get(0));
        assertEquals(forwarderDTO2, result.get(1));
    }

    @Test
    @DisplayName("Should return a specific forwarder by ID")
    void testFindForwarderById() {
        Long forwarderId = 1L;
        Forwarder forwarder = new Forwarder();
        ForwarderDTO forwarderDTO = new ForwarderDTO();
        when(forwarderRepository.findById(forwarderId)).thenReturn(Optional.of(forwarder));
        when(forwarderMapper.mapToDTO(forwarder)).thenReturn(forwarderDTO);

        ForwarderDTO result = forwarderService.findForwarderById(forwarderId);

        assertNotNull(result);
        assertEquals(forwarderDTO, result);
    }

    @Test
    @DisplayName("Should register new Forwarder successfully")
    public void testRegisterNewForwarder_Success() throws RoleNotFoundException {
        ForwarderDTO forwarderDTO = prepareForwarderDTO();
        Forwarder forwarder = prepareForwarder();
        Address address = prepareAddress();
        EmployeeRole role = prepareEmployeeRole();

        when(forwarderMapper.mapToEntity(forwarderDTO)).thenReturn(forwarder);
        when(addressRepository.findById(forwarderDTO.getAddressId())).thenReturn(Optional.of(address));
        when(employeeRoleRepository.findByName("FORWARDER")).thenReturn(Optional.of(role));
        when(userRepository.existsByEmail(forwarderDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(forwarderDTO.getPassword())).thenReturn("encodedPassword");

        String result = forwarderService.registerNewForwarder(forwarderDTO);

        assertNotNull(result);
        assertEquals("User registered successfully!", result);

        verify(forwarderRepository, times(1)).save(forwarder);
    }

    @Test
    @DisplayName("Should update Forwarder successfully")
    public void testUpdateForwarder_Success() {
        ForwarderDTO forwarderDTO = prepareForwarderDTO();
        Forwarder forwarder = prepareForwarder();

        when(forwarderMapper.mapToEntity(forwarderDTO)).thenReturn(forwarder);

        forwarderService.updateForwarder(forwarderDTO);

        verify(forwarderRepository, times(1)).save(forwarder);
    }

    @Test
    @DisplayName("Should throw EmailAddressIsTaken exception when registering Forwarder with existing email")
    public void testRegisterNewForwarder_EmailAddressTaken() throws RoleNotFoundException {
        ForwarderDTO forwarderDTO = prepareForwarderDTO();

        when(userRepository.existsByEmail(forwarderDTO.getEmail())).thenReturn(true);

        assertThrows(EmailAddressIsTaken.class, () -> forwarderService.registerNewForwarder(forwarderDTO));

        verify(forwarderRepository, never()).save(any(Forwarder.class));
    }

    private ForwarderDTO prepareForwarderDTO() {
        ForwarderDTO forwarderDTO = new ForwarderDTO();
        forwarderDTO.setEmail("forwarder@example.com");
        forwarderDTO.setPassword("password123");
        forwarderDTO.setAddressId(1L);
        return forwarderDTO;
    }

    private Forwarder prepareForwarder() {
        Forwarder forwarder = new Forwarder();
        forwarder.setEmail("forwarder@example.com");
        forwarder.setPassword("encodedPassword");
        forwarder.setTotalMargin(BigDecimal.ZERO);
        forwarder.setSalaryBonus(0.0);
        return forwarder;
    }

    private EmployeeRole prepareEmployeeRole() {
        EmployeeRole role = new EmployeeRole();
        role.setId(1L);
        role.setName("FORWARDER");
        return role;
    }

    private Address prepareAddress() {
        Address address = new Address();
        address.setId(1L);
        address.setStreet("Example Street");
        address.setCity("Example City");
        address.setPostalCode("12345");
        return address;
    }
}