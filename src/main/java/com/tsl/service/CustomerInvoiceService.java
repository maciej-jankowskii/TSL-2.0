package com.tsl.service;

import com.tsl.dtos.CustomerInvoiceDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.CustomerInvoiceMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.model.invoice.CustomerInvoice;
import com.tsl.repository.CargoRepository;
import com.tsl.repository.CustomerInvoiceRepository;
import com.tsl.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerInvoiceService {

    private final CargoRepository cargoRepository;
    private final CustomerRepository customerRepository;
    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final CustomerInvoiceMapper customerInvoiceMapper;
    private final VatCalculatorService vatCalculatorService;

    public CustomerInvoiceService(CargoRepository cargoRepository, CustomerRepository customerRepository,
                                  CustomerInvoiceRepository customerInvoiceRepository, CustomerInvoiceMapper customerInvoiceMapper, VatCalculatorService vatCalculatorService) {
        this.cargoRepository = cargoRepository;
        this.customerRepository = customerRepository;
        this.customerInvoiceRepository = customerInvoiceRepository;
        this.customerInvoiceMapper = customerInvoiceMapper;
        this.vatCalculatorService = vatCalculatorService;
    }

    public List<CustomerInvoiceDTO> findAllCustomerInvoices(){
        return customerInvoiceRepository.findAll().stream().map(customerInvoiceMapper::mapToDTO).collect(Collectors.toList());
    }
    public CustomerInvoiceDTO findCustomerInvoiceById(Long id) {
        return customerInvoiceRepository.findById(id).map(customerInvoiceMapper::mapToDTO).orElseThrow(() -> new InvoiceNotFound("Invoice not found"));
    }

    @Transactional
    public CustomerInvoiceDTO addCustomerInvoice(CustomerInvoiceDTO customerInvoiceDTO){
        CustomerInvoice invoice = customerInvoiceMapper.mapToEntity(customerInvoiceDTO);

        Customer customer = extractCustomerFromInvoice(customerInvoiceDTO);
        Cargo cargo = extractCargoFromInvoice(customerInvoiceDTO);

        changeInvoicingStatusForCargo(cargo);
        addAdditionalDataFromInvoice(invoice, cargo, customer);

        CustomerInvoice saved = customerInvoiceRepository.save(invoice);
        return customerInvoiceMapper.mapToDTO(saved);
    }

    @Transactional
    public CustomerInvoiceDTO markInvoiceAsPaid(Long invoiceId){
        CustomerInvoice invoice = customerInvoiceRepository.findById(invoiceId).orElseThrow(() -> new InvoiceNotFound("Invoice not found"));

        if (invoice.getIsPaid()){
            throw new InvoiceAlreadyPaidException("Invoice is already paid");
        }

        changeCustomerBalance(invoice);

        invoice.setIsPaid(true);

        CustomerInvoice saved = customerInvoiceRepository.save(invoice);
        return customerInvoiceMapper.mapToDTO(saved);
    }

    @Transactional
    public void updateCustomerInvoice(CustomerInvoiceDTO currentDTO, CustomerInvoiceDTO updatedDTO) {
        CustomerInvoice customerInvoice = customerInvoiceMapper.mapToEntity(updatedDTO);

        checkingPaidStatus(customerInvoice);
        checkingUnauthorizedValueChange(currentDTO, updatedDTO);

        customerInvoiceRepository.save(customerInvoice);
    }

    private static void checkingUnauthorizedValueChange(CustomerInvoiceDTO currentDTO, CustomerInvoiceDTO updatedDTO) {
        if (currentDTO.getIsPaid() == true && updatedDTO.getIsPaid() == false){
            throw new CannotEditEntityException("Cannot change isPaid value from paid to false");
        }
    }

    private static void checkingPaidStatus(CustomerInvoice customerInvoice) {
        if (customerInvoice.getIsPaid()){
            throw new CannotEditEntityException("Cannot edit customer invoice because is paid.");
        }
    }

    public List<CustomerInvoiceDTO> findAllCustomerInvoicesSortedBy(String sortBy) {
        return customerInvoiceRepository.findAllCustomerInvoicesBy(sortBy).stream().map(customerInvoiceMapper::mapToDTO).collect(Collectors.toList());
    }



    private static void changeCustomerBalance(CustomerInvoice invoice) {
        Customer customer = invoice.getCustomer();
        BigDecimal balance = customer.getBalance();
        BigDecimal grossValue = invoice.getGrossValue();
        customer.setBalance(balance.subtract(grossValue));
    }

    private void addAdditionalDataFromInvoice(CustomerInvoice invoice, Cargo cargo, Customer customer) {
        LocalDate currentDate = LocalDate.now();
        invoice.setInvoiceDate(currentDate);

        Integer termOfPayment = customer.getTermOfPayment();
        LocalDate dueDate = currentDate.plusDays(termOfPayment);

        invoice.setDueDate(dueDate);
        invoice.setNetValue(cargo.getPrice());
        BigDecimal grossValue = vatCalculatorService.calculateGrossValue(cargo.getPrice(), customer.getVatNumber());
        invoice.setGrossValue(grossValue);
        invoice.setIsPaid(false);

    }

    private static void changeInvoicingStatusForCargo(Cargo cargo) {
        cargo.setInvoiced(true);
    }

    private Cargo extractCargoFromInvoice(CustomerInvoiceDTO customerInvoiceDTO) {
        Long cargoId = customerInvoiceDTO.getCargoId();
        return cargoRepository.findById(cargoId).orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
    }

    private Customer extractCustomerFromInvoice(CustomerInvoiceDTO customerInvoiceDTO) {
        Long customerId = customerInvoiceDTO.getCustomerId();
        return customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }
}
