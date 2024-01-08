package com.tsl.service;

import com.tsl.dtos.CargoDTO;
import com.tsl.exceptions.CannotEditEntityException;
import com.tsl.exceptions.CargoNotFoundException;
import com.tsl.exceptions.CustomerNotFoundException;
import com.tsl.exceptions.WrongLoadigDateException;
import com.tsl.mapper.CargoMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.model.employee.User;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.CustomerRepository;
import com.tsl.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CargoServiceTest {
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private CargoMapper cargoMapper;
    @Mock
    private VatCalculatorService vatCalculatorService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CargoService cargoService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all Cargos")
    public void testFindAllCargos_Success() {
        Cargo cargo1 = prepareFirstCargo();
        Cargo cargo2 = prepareSecondCargo();
        CargoDTO cargoDTO1 = prepareFirstDTO();
        CargoDTO cargoDTO2 = prepareSecondDTO();
        List<Cargo> cargos = Arrays.asList(cargo1, cargo2);

        when(cargoRepository.findAll()).thenReturn(cargos);
        when(cargoMapper.mapToDTO(cargo1)).thenReturn(cargoDTO1);
        when(cargoMapper.mapToDTO(cargo2)).thenReturn(cargoDTO2);
        List<CargoDTO> resultCargos = cargoService.findAllCargos();

        assertNotNull(resultCargos);
        assertEquals(resultCargos.size(), cargos.size());
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find all not assigned Cargos")
    public void testFindAllNotAssignedCargos_Success() {
        Cargo cargo1 = prepareFirstCargo();
        Cargo cargo2 = prepareSecondCargo();
        CargoDTO cargoDTO1 = prepareFirstDTO();
        CargoDTO cargoDTO2 = prepareSecondDTO();
        List<Cargo> cargos = Arrays.asList(cargo1, cargo2);

        when(cargoRepository.findAllByAssignedToOrderIsFalse()).thenReturn(cargos);
        when(cargoMapper.mapToDTO(cargo1)).thenReturn(cargoDTO1);
        when(cargoMapper.mapToDTO(cargo2)).thenReturn(cargoDTO2);
        List<CargoDTO> resultCargos = cargoService.findAllNotAssignedCargos();

        assertNotNull(resultCargos);
        assertEquals(resultCargos.size(), cargos.size());
        verify(cargoRepository, times(1)).findAllByAssignedToOrderIsFalse();
    }

    @Test
    @DisplayName("Should find all not invoiced Cargos")
    public void testFindAllNotInvoicedCargos_Success() {
        Cargo cargo1 = prepareFirstCargo();
        Cargo cargo2 = prepareSecondCargo();
        CargoDTO cargoDTO1 = prepareFirstDTO();
        CargoDTO cargoDTO2 = prepareSecondDTO();
        List<Cargo> cargos = Arrays.asList(cargo1, cargo2);

        when(cargoRepository.findAllByInvoicedFalse()).thenReturn(cargos);
        when(cargoMapper.mapToDTO(cargo1)).thenReturn(cargoDTO1);
        when(cargoMapper.mapToDTO(cargo2)).thenReturn(cargoDTO2);
        List<CargoDTO> resultCargos = cargoService.findAllNotInvoicedCargos();

        assertNotNull(resultCargos);
        assertEquals(resultCargos.size(), cargos.size());
        verify(cargoRepository, times(1)).findAllByInvoicedFalse();
    }

    @Test
    @DisplayName("Should find Cargo by ID")
    public void testFindCargoById_Success() {
        Cargo cargo = prepareFirstCargo();
        CargoDTO cargoDTO = prepareFirstDTO();

        when(cargoRepository.findById(cargo.getId())).thenReturn(Optional.of(cargo));
        when(cargoMapper.mapToDTO(cargo)).thenReturn(cargoDTO);

        CargoDTO resultCargo = cargoService.findCargoById(cargo.getId());

        assertEquals(resultCargo, cargoDTO);
        verify(cargoRepository, times(1)).findById(cargo.getId());

    }

    @Test
    @DisplayName("Should throw CargoNotFoundException")
    public void testFindCargoById_CargoNotFound() {
        Long cargoId = 1L;

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.empty());

        assertThrows(CargoNotFoundException.class, () -> cargoService.findCargoById(cargoId));
    }

    @Test
    @DisplayName("Should add Cargo successfully")
    public void testAddCargo_Success() {
        CargoDTO cargoDTO = prepareSecondDTO();
        Customer customer = prepareCustomer();
        Cargo cargo = prepareSecondCargo();
        cargo.setCustomer(customer);
        setLoggedInUser("test@example.com");

        when(cargoMapper.mapToEntity(cargoDTO)).thenReturn(cargo);
        when(customerRepository.findById(cargoDTO.getCustomerId())).thenReturn(Optional.of(customer));
        when(cargoRepository.save(cargo)).thenReturn(cargo);
        when(vatCalculatorService.calculateGrossValue(any(), any())).thenReturn(BigDecimal.TEN);
        when(cargoMapper.mapToDTO(cargo)).thenReturn(cargoDTO);

        CargoDTO resultDTO = cargoService.addCargo(cargoDTO);

        assertNotNull(resultDTO);
    }

    @Test
    @DisplayName("Should change customer balance after adding Cargo")
    public void testAddCargo_ChangeCustomerBalance() {
        CargoDTO cargoDTO = prepareSecondDTO();
        cargoDTO.setCustomerId(1L);

        Customer customer = prepareCustomer();
        BigDecimal initialBalance = customer.getBalance();

        Cargo cargo = prepareSecondCargo();
        cargo.setCustomer(customer);
        setLoggedInUser("test@exmaple.com");

        when(cargoMapper.mapToEntity(cargoDTO)).thenReturn(cargo);
        when(customerRepository.findById(cargoDTO.getCustomerId())).thenReturn(Optional.of(customer));
        when(cargoRepository.save(cargo)).thenReturn(cargo);
        when(vatCalculatorService.calculateGrossValue(any(), any())).thenReturn(cargo.getPrice());
        when(cargoMapper.mapToDTO(cargo)).thenReturn(cargoDTO);

        CargoDTO resultDTO = cargoService.addCargo(cargoDTO);

        assertNotNull(resultDTO);
        BigDecimal finalBalance = customer.getBalance();

        assertTrue(finalBalance.compareTo(initialBalance) > 0);

        verify(customerRepository, times(1)).save(customer);
        verify(cargoRepository, times(1)).save(cargo);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when adding Cargo with non-existent customer ID")
    public void testAddCargo_NonExistentCustomer() {
        CargoDTO cargoDTO = prepareSecondDTO();

        when(cargoMapper.mapToEntity(cargoDTO)).thenReturn(prepareSecondCargo());
        when(customerRepository.findById(cargoDTO.getCustomerId())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> cargoService.addCargo(cargoDTO));
        verify(customerRepository, never()).save(any());
        verify(cargoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw WrongLoadingDateException when adding Cargo with invalid unloading date")
    public void testAddCargo_InvalidUnloadingDate() {
        CargoDTO cargoDTO = prepareSecondDTO();
        Cargo cargo = prepareSecondCargo();
        cargo.setUnloadingDate(LocalDate.now().minusDays(1));
        setLoggedInUser("test@example.com");

        when(cargoMapper.mapToEntity(cargoDTO)).thenReturn(cargo);
        when(customerRepository.findById(cargoDTO.getCustomerId())).thenReturn(Optional.of(new Customer()));

        assertThrows(WrongLoadigDateException.class, () -> cargoService.addCargo(cargoDTO));

        verify(customerRepository, never()).save(any());
        verify(cargoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update Cargo successfully")
    public void testUpdateCargo_Success() {
        CargoDTO currentDTO = prepareNotAssignedDTO();
        CargoDTO updatedDTO = prepareSecondNotAssignedDTO();
        Cargo cargo = prepareNotAssignedCargo();

        when(cargoMapper.mapToEntity(updatedDTO)).thenReturn(cargo);
        when(cargoRepository.findById(updatedDTO.getId())).thenReturn(Optional.of(cargo));
        when(cargoRepository.save(cargo)).thenReturn(cargo);

        assertDoesNotThrow(() -> cargoService.updateCargo(currentDTO, updatedDTO));

        verify(cargoRepository, times(1)).save(cargo);
    }

    @Test
    @DisplayName("Should throw CannotEditEntityException when updating assigned Cargo")
    public void testUpdateCargo_AssignedCargo() {
        CargoDTO currentDTO = prepareFirstDTO();
        CargoDTO updatedDTO = prepareSecondDTO();
        updatedDTO.setAssignedToOrder(true);

        Cargo cargo = prepareFirstCargo();

        when(cargoMapper.mapToEntity(updatedDTO)).thenReturn(cargo);

        assertThrows(CannotEditEntityException.class, () -> cargoService.updateCargo(currentDTO, updatedDTO));

        verify(cargoRepository, never()).save(cargo);
    }

    @Test
    @DisplayName("Should throw CannotEditEntityException when changing isInvoiced from true to false")
    public void testUpdateCargo_ChangeInvoicedFromTrueToFalse() {
        CargoDTO currentDTO = prepareFirstDTO();
        CargoDTO updatedDTO = prepareSecondDTO();
        currentDTO.setInvoiced(true);
        updatedDTO.setInvoiced(false);

        Cargo cargo = prepareFirstCargo();

        when(cargoMapper.mapToEntity(updatedDTO)).thenReturn(cargo);
        when(cargoRepository.findById(updatedDTO.getId())).thenReturn(Optional.of(cargo));

        assertThrows(CannotEditEntityException.class, () -> cargoService.updateCargo(currentDTO, updatedDTO));

        verify(cargoRepository, never()).save(cargo);
    }

    @Test
    @DisplayName("Should delete Cargo successfully")
    public void testDeleteCargo_Success() {
        Long cargoId = 1L;
        Cargo cargo = prepareNotAssignedCargo();
        Customer customer = prepareCustomer();
        cargo.setCustomer(customer);
        customer.setCargos(Collections.singletonList(cargo));

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(vatCalculatorService.calculateGrossValue(any(), any())).thenReturn(cargo.getPrice());


        cargoService.deleteCargo(cargoId);

        assertEquals(BigDecimal.valueOf(1000), customer.getBalance());
        verify(cargoRepository, times(1)).deleteById(eq(cargoId));
    }

    @Test
    @DisplayName("Should throw CargoNotFoundException when deleting non-existent Cargo")
    public void testDeleteCargo_NonExistentCargo() {
        Long cargoId = 1L;

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.empty());

        assertThrows(CargoNotFoundException.class, () -> cargoService.deleteCargo(cargoId));

        verify(cargoRepository, never()).deleteById(any());
    }

    private void setLoggedInUser(String userEmail) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);

        User user = new User();
        user.setEmail(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private CargoDTO prepareSecondDTO() {
        CargoDTO dto = new CargoDTO();
        dto.setId(2L);
        dto.setAssignedToOrder(false);
        dto.setInvoiced(false);
        dto.setPrice(BigDecimal.valueOf(1000.0));
        dto.setLoadingDate(LocalDate.now());
        dto.setUnloadingDate(LocalDate.now().plusDays(2));
        return dto;
    }

    private CargoDTO prepareNotAssignedDTO() {
        CargoDTO dto = new CargoDTO();
        dto.setId(2L);
        dto.setAssignedToOrder(false);
        dto.setInvoiced(false);
        dto.setPrice(BigDecimal.valueOf(1000.0));
        dto.setLoadingDate(LocalDate.now());
        dto.setUnloadingDate(LocalDate.now().plusDays(2));
        return dto;
    }

    private CargoDTO prepareSecondNotAssignedDTO() {
        CargoDTO dto = new CargoDTO();
        dto.setId(2L);
        dto.setAssignedToOrder(false);
        dto.setInvoiced(false);
        dto.setPrice(BigDecimal.valueOf(1000.0));
        dto.setLoadingDate(LocalDate.now());
        dto.setUnloadingDate(LocalDate.now().plusDays(2));
        return dto;
    }

    private CargoDTO prepareFirstDTO() {
        CargoDTO dto = new CargoDTO();
        dto.setId(1L);
        dto.setAssignedToOrder(true);
        dto.setInvoiced(true);
        return dto;
    }

    private Cargo prepareSecondCargo() {
        Cargo cargo = new Cargo();
        cargo.setId(2L);
        cargo.setAssignedToOrder(false);
        cargo.setInvoiced(false);
        cargo.setPrice(BigDecimal.valueOf(1000.0));
        cargo.setLoadingDate(LocalDate.now());
        cargo.setUnloadingDate(LocalDate.now().plusDays(2));
        return cargo;
    }


    private Cargo prepareFirstCargo() {
        Cargo cargo = new Cargo();
        cargo.setId(1L);
        cargo.setAssignedToOrder(true);
        cargo.setInvoiced(true);
        return cargo;
    }

    private Cargo prepareNotAssignedCargo() {
        Cargo cargo = new Cargo();
        cargo.setId(1L);
        cargo.setAssignedToOrder(false);
        cargo.setInvoiced(false);
        cargo.setPrice(BigDecimal.valueOf(1000));
        return cargo;
    }

    private Customer prepareCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setVatNumber("PL123456789");
        customer.setBalance(BigDecimal.valueOf(2000));
        return customer;
    }

}