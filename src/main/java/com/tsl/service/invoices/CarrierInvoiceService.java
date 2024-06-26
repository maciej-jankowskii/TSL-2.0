package com.tsl.service.invoices;

import com.tsl.dtos.invoices.CarrierInvoiceDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.CarrierInvoiceMapper;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.invoice.CarrierInvoice;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.repository.invoices.CarrierInvoiceRepository;
import com.tsl.repository.forwardingAndTransport.CarrierRepository;
import com.tsl.repository.forwardingAndTransport.ForwarderOrderRepository;
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
public class CarrierInvoiceService {
    private final CarrierInvoiceRepository carrierInvoiceRepository;
    private final CarrierInvoiceMapper carrierInvoiceMapper;
    private final VatCalculatorService vatCalculatorService;
    private final ForwarderOrderRepository forwarderOrderRepository;
    private final CarrierRepository carrierRepository;


    /***
     Finding methods
     */

    public List<CarrierInvoiceDTO> findAll() {
        return carrierInvoiceRepository.findAll().stream().map(carrierInvoiceMapper::mapToDTO).collect(Collectors.toList());
    }

    public CarrierInvoiceDTO findCarrierInvoiceById(Long id) {
        return carrierInvoiceRepository.findById(id).map(carrierInvoiceMapper::mapToDTO)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
    }

    public List<CarrierInvoiceDTO> findAllCarrierInvoicesSortedBy(String sortBy) {
        return carrierInvoiceRepository.findAllCarrierInvoicesBy(sortBy).stream().map(carrierInvoiceMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    /***
     Create and update methods
     */

    @Transactional
    public CarrierInvoiceDTO addCarrierInvoice(CarrierInvoiceDTO carrierInvoiceDTO) {
        CarrierInvoice invoice = carrierInvoiceMapper.mapToEntity(carrierInvoiceDTO);

        Carrier carrier = carrierRepository.findById(carrierInvoiceDTO.getCarrierId())
                .orElseThrow(() -> new CarrierNotFoundException("Carrier not found"));
        ForwardingOrder order = forwarderOrderRepository.findById(carrierInvoiceDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        addAdditionalDataFroInvoice(invoice, order, carrier);

        CarrierInvoice saved = carrierInvoiceRepository.save(invoice);
        return carrierInvoiceMapper.mapToDTO(saved);
    }

    @Transactional
    public void markInvoiceAsPaid(Long invoiceId) {
        CarrierInvoice invoice = carrierInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
        if (invoice.getIsPaid()) {
            throw new InvoiceAlreadyPaidException("Invoice is already paid");
        }
        changeCarrierBalance(invoice);

        invoice.setIsPaid(true);

        carrierInvoiceRepository.save(invoice);
    }

    @Transactional
    public void updateCarrierInvoice(CarrierInvoiceDTO currentDTO, CarrierInvoiceDTO updatedDTO) {
        CarrierInvoice carrierInvoice = carrierInvoiceMapper.mapToEntity(updatedDTO);

        checkingPaidStatus(carrierInvoice);
        checkingUnauthorizedValueChange(currentDTO, updatedDTO);

        carrierInvoiceRepository.save(carrierInvoice);
    }

    /***
     Helper methods
     */

    private static void checkingUnauthorizedValueChange(CarrierInvoiceDTO currentDTO, CarrierInvoiceDTO updatedDTO) {
        if (currentDTO.getIsPaid() == true && updatedDTO.getIsPaid() == false) {
            throw new CannotEditEntityException("Cannot change isPaid value from paid to false");
        }
    }

    private static void checkingPaidStatus(CarrierInvoice carrierInvoice) {
        if (carrierInvoice.getIsPaid()) {
            throw new CannotEditEntityException("Cannot edit carrier invoice because is paid.");
        }
    }

    private static void changeCarrierBalance(CarrierInvoice invoice) {
        Carrier carrier = invoice.getCarrier();
        BigDecimal grossValue = invoice.getGrossValue();
        BigDecimal balance = carrier.getBalance();
        carrier.setBalance(balance.subtract(grossValue));
    }

    private void addAdditionalDataFroInvoice(CarrierInvoice invoice, ForwardingOrder order, Carrier carrier) {
        invoice.setOrder(order);
        invoice.setCarrier(carrier);

        LocalDate currentDate = LocalDate.now();
        invoice.setInvoiceDate(currentDate);

        Integer termOfPayment = carrier.getTermOfPayment();
        LocalDate dueDate = currentDate.plusDays(termOfPayment);
        invoice.setDueDate(dueDate);

        invoice.setNetValue(order.getPrice());
        BigDecimal grossValue = vatCalculatorService.calculateGrossValue(order.getPrice(), carrier.getVatNumber());
        invoice.setGrossValue(grossValue);
        invoice.setIsPaid(false);
    }
}
