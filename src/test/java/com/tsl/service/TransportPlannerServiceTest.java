package com.tsl.service;

import com.tsl.dtos.TransportPlannerDTO;
import com.tsl.exceptions.AddressNotFoundException;
import com.tsl.exceptions.EmailAddressIsTaken;
import com.tsl.exceptions.EmployeeNotFoundException;
import com.tsl.mapper.TransportPlannerMapper;
import com.tsl.model.address.Address;
import com.tsl.model.employee.TransportPlanner;
import com.tsl.model.role.EmployeeRole;
import com.tsl.model.truck.Truck;
import com.tsl.repository.*;
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

class TransportPlannerServiceTest {

    @Mock
    private TransportPlannerRepository transportPlannerRepository;
    @Mock
    private TransportPlannerMapper transportPlannerMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SalaryBonusCalculator salaryBonusCalculator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployeeRoleRepository employeeRoleRepository;
    @Mock
    private TruckRepository truckRepository;
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private TransportPlannerService transportPlannerService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Transport Planners successfully")
    public void testFindAllTransportPlanners_Success() {
        TransportPlanner planner1 = prepareTransportPlanner();
        TransportPlanner planner2 = prepareTransportPlanner();
        TransportPlannerDTO plannerDTO1 = prepareTransportPlannerDTO();
        TransportPlannerDTO plannerDTO2 = prepareTransportPlannerDTO();

        when(transportPlannerRepository.findAll()).thenReturn(Arrays.asList(planner1, planner2));
        when(transportPlannerMapper.mapToDTO(planner1)).thenReturn(plannerDTO1);
        when(transportPlannerMapper.mapToDTO(planner2)).thenReturn(plannerDTO2);

        List<TransportPlannerDTO> resultPlanners = transportPlannerService.findAllTransportPlanners();

        assertNotNull(resultPlanners);
        assertEquals(2, resultPlanners.size());

        verify(transportPlannerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find Transport Planner by ID successfully")
    public void testFindPlannerById_Success() {
        Long plannerId = 1L;
        TransportPlanner planner = prepareTransportPlanner();
        TransportPlannerDTO plannerDTO = prepareTransportPlannerDTO();

        when(transportPlannerRepository.findById(plannerId)).thenReturn(Optional.of(planner));
        when(transportPlannerMapper.mapToDTO(planner)).thenReturn(plannerDTO);

        TransportPlannerDTO resultPlanner = transportPlannerService.findPlannerById(plannerId);

        assertNotNull(resultPlanner);
        assertEquals("John", resultPlanner.getFirstName());

        verify(transportPlannerRepository, times(1)).findById(plannerId);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when Transport Planner is not found by ID")
    public void testFindPlannerById_PlannerNotFound() {
        Long plannerId = 1L;

        when(transportPlannerRepository.findById(plannerId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> transportPlannerService.findPlannerById(plannerId));

        verify(transportPlannerMapper, never()).mapToDTO(any());
    }

    @Test
    @DisplayName("Should register new Transport Planner successfully")
    public void testRegisterNewTransportPlanner_Success() throws RoleNotFoundException {
        TransportPlannerDTO plannerDTO = prepareTransportPlannerDTO();
        TransportPlanner planner = prepareTransportPlanner();
        Address address = prepareAddress();
        EmployeeRole role = prepareEmployeeRole();

        when(transportPlannerMapper.mapToEntity(plannerDTO)).thenReturn(planner);
        when(addressRepository.findById(plannerDTO.getAddressId())).thenReturn(Optional.of(address));
        when(truckRepository.findById(anyLong())).thenReturn(Optional.of(prepareTruck()));
        when(salaryBonusCalculator.calculateSalaryBonusForPlanners(planner)).thenReturn(1000.0);
        when(employeeRoleRepository.findByName("PLANNER")).thenReturn(Optional.of(role));
        when(userRepository.existsByEmail(plannerDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(plannerDTO.getPassword())).thenReturn("encodedPassword");

        String result = transportPlannerService.registerNewTransportPlanner(plannerDTO);

        assertNotNull(result);
        assertEquals("User registered successfully!", result);

        verify(transportPlannerRepository, times(1)).save(planner);
    }

    @Test
    @DisplayName("Should throw AddressNotFoundException when Address is not found during Transport Planner registration")
    public void testRegisterNewTransportPlanner_AddressNotFound() {
        TransportPlannerDTO plannerDTO = prepareTransportPlannerDTO();
        EmployeeRole role = prepareEmployeeRole();

        when(transportPlannerMapper.mapToEntity(plannerDTO)).thenReturn(prepareTransportPlanner());
        when(addressRepository.findById(plannerDTO.getAddressId())).thenReturn(Optional.empty());
        when(salaryBonusCalculator.calculateSalaryBonusForPlanners(any())).thenReturn(1000.0);
        when(employeeRoleRepository.findByName("PLANNER")).thenReturn(Optional.of(role));
        when(userRepository.existsByEmail(plannerDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(plannerDTO.getPassword())).thenReturn("encodedPassword");

        assertThrows(AddressNotFoundException.class, () -> transportPlannerService.registerNewTransportPlanner(plannerDTO));

        verify(transportPlannerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when Role is not found during Transport Planner registration")
    public void testRegisterNewTransportPlanner_RoleNotFound() {
        TransportPlannerDTO plannerDTO = prepareTransportPlannerDTO();

        when(transportPlannerMapper.mapToEntity(plannerDTO)).thenReturn(prepareTransportPlanner());
        when(addressRepository.findById(plannerDTO.getAddressId())).thenReturn(Optional.of(prepareAddress()));
        when(salaryBonusCalculator.calculateSalaryBonusForPlanners(any())).thenReturn(1000.0);
        when(employeeRoleRepository.findByName("PLANNER")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(plannerDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(plannerDTO.getPassword())).thenReturn("encodedPassword");

        assertThrows(RoleNotFoundException.class, () -> transportPlannerService.registerNewTransportPlanner(plannerDTO));

        verify(transportPlannerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EmailAddressIsTaken when email is already taken during Transport Planner registration")
    public void testRegisterNewTransportPlanner_EmailAddressTaken() throws RoleNotFoundException {
        TransportPlannerDTO plannerDTO = prepareTransportPlannerDTO();
        EmployeeRole role = prepareEmployeeRole();

        when(transportPlannerMapper.mapToEntity(plannerDTO)).thenReturn(prepareTransportPlanner());
        when(addressRepository.findById(plannerDTO.getAddressId())).thenReturn(Optional.of(prepareAddress()));
        when(salaryBonusCalculator.calculateSalaryBonusForPlanners(any())).thenReturn(1000.0);
        when(employeeRoleRepository.findByName("PLANNER")).thenReturn(Optional.of(role));
        when(userRepository.existsByEmail(plannerDTO.getEmail())).thenReturn(true);
        when(passwordEncoder.encode(plannerDTO.getPassword())).thenReturn("encodedPassword");

        assertThrows(EmailAddressIsTaken.class, () -> transportPlannerService.registerNewTransportPlanner(plannerDTO));

        verify(transportPlannerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Transport Planner successfully")
    public void testUpdatePlanner_Success() {
        TransportPlannerDTO plannerDTO = prepareTransportPlannerDTO();
        TransportPlanner planner = prepareTransportPlanner();

        when(transportPlannerMapper.mapToEntity(plannerDTO)).thenReturn(planner);
        when(transportPlannerRepository.save(planner)).thenReturn(planner);

        transportPlannerService.updatePlanner(plannerDTO);

        verify(transportPlannerRepository, times(1)).save(planner);
    }

    private TransportPlanner prepareTransportPlanner() {
        TransportPlanner planner = new TransportPlanner();
        planner.setId(1L);
        planner.setFirstName("John");
        planner.setLastName("Doe");
        planner.setEmail("john.doe@example.com");
        planner.setPassword("encodedPassword");
        planner.setAddress(prepareAddress());
        planner.setSalaryBonus(1000.0);
        planner.setRoles(Collections.singletonList(prepareEmployeeRole()));
        planner.setCompanyTrucks(Arrays.asList(prepareTruck(), prepareTruck()));
        return planner;
    }

    private TransportPlannerDTO prepareTransportPlannerDTO() {
        TransportPlannerDTO plannerDTO = new TransportPlannerDTO();
        plannerDTO.setId(1L);
        plannerDTO.setFirstName("John");
        plannerDTO.setLastName("Doe");
        plannerDTO.setEmail("john.doe@example.com");
        plannerDTO.setPassword("password");
        plannerDTO.setAddressId(2L);
        plannerDTO.setTruckIds(Arrays.asList(3L, 4L));
        return plannerDTO;
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
        role.setName("PLANNER");
        return role;
    }

    private Truck prepareTruck() {
        Truck truck = new Truck();
        truck.setId(3L);
        truck.setPlates("ABC123/ABC123");
        return truck;
    }

}