package com.tsl.service.invoices;

import com.tsl.dtos.invoices.CustomerInvoiceDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.CustomerInvoiceMapper;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import com.tsl.model.invoice.CustomerInvoice;
import com.tsl.repository.forwardingAndTransport.CargoRepository;
import com.tsl.repository.invoices.CustomerInvoiceRepository;
import com.tsl.repository.forwardingAndTransport.CustomerRepository;
import com.tsl.service.calculators.VatCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerInvoiceService {

    private final CargoRepository cargoRepository;
    private final CustomerRepository customerRepository;
    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final CustomerInvoiceMapper customerInvoiceMapper;
    private final VatCalculatorService vatCalculatorService;


    /***
     Finding methods
     */

    public List<CustomerInvoiceDTO> findAllCustomerInvoices() {
        return customerInvoiceRepository.findAll().stream().map(customerInvoiceMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public CustomerInvoiceDTO findCustomerInvoiceById(Long id) {
        return customerInvoiceRepository.findById(id).map(customerInvoiceMapper::mapToDTO)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
    }

    public List<CustomerInvoiceDTO> findAllCustomerInvoicesSortedBy(String sortBy) {
        return customerInvoiceRepository.findAllCustomerInvoicesBy(sortBy).stream().map(customerInvoiceMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CustomerInvoiceDTO> findAllOverdueInvoices() {
        LocalDate currentDate = LocalDate.now();
        List<CustomerInvoice> allInvoices = customerInvoiceRepository.findAll();

        return allInvoices.stream()
                .filter(invoice -> invoice.getDueDate().isBefore(currentDate) && !invoice.getIsPaid())
                .map(customerInvoiceMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    /***
     Create and update methods
     */

    @Transactional
    public CustomerInvoiceDTO addCustomerInvoice(CustomerInvoiceDTO customerInvoiceDTO) {
        CustomerInvoice invoice = customerInvoiceMapper.mapToEntity(customerInvoiceDTO);

        Cargo cargo = cargoRepository.findById(customerInvoiceDTO.getCargoId())
                .orElseThrow(() -> new CargoNotFoundException("Cargo not found"));
        Customer customer = customerRepository.findById(customerInvoiceDTO.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        changeInvoicingStatusForCargo(cargo);
        addAdditionalDataFromInvoice(invoice, cargo, customer);

        CustomerInvoice saved = customerInvoiceRepository.save(invoice);
        return customerInvoiceMapper.mapToDTO(saved);
    }

    @Transactional
    public CustomerInvoiceDTO markInvoiceAsPaid(Long invoiceId) {
        CustomerInvoice invoice = customerInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));

        checkingIsPaidInvoice(invoice);
        changeCustomerBalance(invoice);

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

    /***
     Helper methods
     */

    private static void checkingUnauthorizedValueChange(CustomerInvoiceDTO currentDTO, CustomerInvoiceDTO updatedDTO) {
        if (currentDTO.getIsPaid() == true && updatedDTO.getIsPaid() == false) {
            throw new CannotEditEntityException("Cannot change isPaid value from paid to false");
        }
    }

    private static void checkingPaidStatus(CustomerInvoice customerInvoice) {
        if (customerInvoice.getIsPaid()) {
            throw new CannotEditEntityException("Cannot edit customer invoice because is paid.");
        }
    }

    private static void checkingIsPaidInvoice(CustomerInvoice invoice) {
        if (invoice.getIsPaid()) {
            throw new InvoiceAlreadyPaidException("Invoice is already paid");
        }
        invoice.setIsPaid(true);
    }

    private static void changeCustomerBalance(CustomerInvoice invoice) {
        Customer customer = invoice.getCustomer();
        BigDecimal balance = customer.getBalance();
        BigDecimal grossValue = invoice.getGrossValue();
        customer.setBalance(balance.subtract(grossValue));
    }

    private void addAdditionalDataFromInvoice(CustomerInvoice invoice, Cargo cargo, Customer customer) {
        invoice.setCustomer(customer);
        invoice.setCargo(cargo);

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

}
