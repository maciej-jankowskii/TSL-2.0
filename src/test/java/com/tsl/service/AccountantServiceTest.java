package com.tsl.service;

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
import com.tsl.service.employees.AccountantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountantServiceTest {

    @Mock
    private AccountantRepository accountantRepository;
    @Mock
    private AccountantMapper accountantMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmployeeRoleRepository employeeRoleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AccountantService accountantService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Accountants successfully")
    public void testFindAllAccountants_Success() {
        Accountant accountant1 = prepareAccountant();
        Accountant accountant2 = prepareAccountant();
        AccountantDTO accountantDTO1 = prepareAccountantDTO();
        AccountantDTO accountantDTO2 = prepareAccountantDTO();

        when(accountantRepository.findAll()).thenReturn(Arrays.asList(accountant1, accountant2));
        when(accountantMapper.mapToDTO(accountant1)).thenReturn(accountantDTO1);
        when(accountantMapper.mapToDTO(accountant2)).thenReturn(accountantDTO2);

        List<AccountantDTO> resultAccountants = accountantService.findAllAccountants();

        assertNotNull(resultAccountants);
        assertEquals(2, resultAccountants.size());

        verify(accountantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Accountant by ID successfully")
    public void testFindAccountantById_Success() {
        Long accountantId = 1L;
        Accountant accountant = prepareAccountant();
        AccountantDTO accountantDTO = prepareAccountantDTO();

        when(accountantRepository.findById(accountantId)).thenReturn(Optional.of(accountant));
        when(accountantMapper.mapToDTO(accountant)).thenReturn(accountantDTO);

        AccountantDTO resultAccountant = accountantService.findAccountantById(accountantId);

        assertNotNull(resultAccountant);
        assertEquals("John", resultAccountant.getFirstName());

        verify(accountantRepository, times(1)).findById(accountantId);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when Accountant is not found by ID")
    public void testFindAccountantById_AccountantNotFound() {
        Long accountantId = 1L;

        when(accountantRepository.findById(accountantId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> accountantService.findAccountantById(accountantId));

        verify(accountantMapper, never()).mapToDTO(any());
    }

    @Test
    @DisplayName("Should register new Accountant successfully")
    public void testRegisterNewAccountant_Success() throws RoleNotFoundException {
        AccountantDTO accountantDTO = prepareAccountantDTO();
        Accountant accountant = prepareAccountant();
        Address address = prepareAddress();
        EmployeeRole role = prepareEmployeeRole();

        when(accountantMapper.mapToEntity(accountantDTO)).thenReturn(accountant);
        when(addressRepository.findById(accountantDTO.getAddressId())).thenReturn(Optional.of(address));
        when(employeeRoleRepository.findByName("ACCOUNTANT")).thenReturn(Optional.of(role));
        when(userRepository.existsByEmail(accountantDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(accountantDTO.getPassword())).thenReturn("encodedPassword");

        String result = accountantService.registerNewAccountant(accountantDTO);

        assertNotNull(result);
        assertEquals("User registered successfully!", result);

        verify(accountantRepository, times(1)).save(accountant);
    }

    @Test
    @DisplayName("Should throw AddressNotFoundException when Address is not found during Accountant registration")
    public void testRegisterNewAccountant_AddressNotFound() {
        AccountantDTO accountantDTO = prepareAccountantDTO();
        EmployeeRole role = prepareEmployeeRole();

        when(accountantMapper.mapToEntity(accountantDTO)).thenReturn(prepareAccountant());
        when(addressRepository.findById(accountantDTO.getAddressId())).thenReturn(Optional.empty());
        when(employeeRoleRepository.findByName("ACCOUNTANT")).thenReturn(Optional.of(role));
        when(userRepository.existsByEmail(accountantDTO.getEmail())).thenReturn(false);

        assertThrows(AddressNotFoundException.class, () -> accountantService.registerNewAccountant(accountantDTO));

        verify(accountantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when Role is not found during Accountant registration")
    public void testRegisterNewAccountant_RoleNotFound() {
        AccountantDTO accountantDTO = prepareAccountantDTO();

        when(accountantMapper.mapToEntity(accountantDTO)).thenReturn(prepareAccountant());
        when(addressRepository.findById(accountantDTO.getAddressId())).thenReturn(Optional.of(prepareAddress()));
        when(employeeRoleRepository.findByName("ACCOUNTANT")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(accountantDTO.getEmail())).thenReturn(false);

        assertThrows(RoleNotFoundException.class, () -> accountantService.registerNewAccountant(accountantDTO));

        verify(accountantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EmailAddressIsTaken when email is already taken during Accountant registration")
    public void testRegisterNewAccountant_EmailAddressTaken() throws RoleNotFoundException {
        AccountantDTO accountantDTO = prepareAccountantDTO();
        EmployeeRole role = prepareEmployeeRole();

        when(accountantMapper.mapToEntity(accountantDTO)).thenReturn(prepareAccountant());
        when(addressRepository.findById(accountantDTO.getAddressId())).thenReturn(Optional.of(prepareAddress()));
        when(employeeRoleRepository.findByName("ACCOUNTANT")).thenReturn(Optional.of(role));
        when(userRepository.existsByEmail(accountantDTO.getEmail())).thenReturn(true);

        assertThrows(EmailAddressIsTaken.class, () -> accountantService.registerNewAccountant(accountantDTO));

        verify(accountantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Accountant successfully")
    public void testUpdateAccountant_Success() {
        AccountantDTO accountantDTO = prepareAccountantDTO();
        Accountant accountant = prepareAccountant();

        when(accountantMapper.mapToEntity(accountantDTO)).thenReturn(accountant);
        when(accountantRepository.save(accountant)).thenReturn(accountant);

        assertDoesNotThrow(() -> accountantService.updateAccountant(accountantDTO));

        verify(accountantRepository, times(1)).save(accountant);
    }

    private AccountantDTO prepareAccountantDTO() {
        AccountantDTO accountantDTO = new AccountantDTO();
        accountantDTO.setId(1L);
        accountantDTO.setFirstName("John");
        accountantDTO.setLastName("Doe");
        accountantDTO.setEmail("john.doe@example.com");
        accountantDTO.setAddressId(2L);
        accountantDTO.setPassword("password");
        return accountantDTO;
    }

    private Accountant prepareAccountant() {
        Accountant accountant = new Accountant();
        accountant.setId(1L);
        accountant.setFirstName("John");
        accountant.setLastName("Doe");
        accountant.setEmail("john.doe@example.com");
        accountant.setAddress(prepareAddress());
        accountant.setRoles(Collections.singletonList(prepareEmployeeRole()));
        accountant.setPassword("encodedPassword");
        return accountant;
    }

    private Address prepareAddress() {
        Address address = new Address();
        address.setId(2L);
        address.setStreet("Main Street");
        address.setCity("City");
        address.setPostalCode("12345");
        return address;
    }

    private EmployeeRole prepareEmployeeRole() {
        EmployeeRole role = new EmployeeRole();
        role.setId(3L);
        role.setName("ACCOUNTANT");
        return role;
    }

}