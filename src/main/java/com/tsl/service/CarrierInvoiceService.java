package com.tsl.service;

import com.tsl.dtos.CarrierInvoiceDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.CarrierInvoiceMapper;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.invoice.CarrierInvoice;
import com.tsl.model.order.ForwardingOrder;
import com.tsl.repository.CarrierInvoiceRepository;
import com.tsl.repository.CarrierRepository;
import com.tsl.repository.ForwarderOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierInvoiceService {
    private final CarrierInvoiceRepository carrierInvoiceRepository;
    private final CarrierInvoiceMapper carrierInvoiceMapper;
    private final VatCalculatorService vatCalculatorService;
    private final ForwarderOrderRepository forwarderOrderRepository;
    private final CarrierRepository carrierRepository;

    public CarrierInvoiceService(CarrierInvoiceRepository carrierInvoiceRepository, CarrierInvoiceMapper carrierInvoiceMapper,
                                 VatCalculatorService vatCalculatorService, ForwarderOrderRepository forwarderOrderRepository, CarrierRepository carrierRepository) {
        this.carrierInvoiceRepository = carrierInvoiceRepository;
        this.carrierInvoiceMapper = carrierInvoiceMapper;
        this.vatCalculatorService = vatCalculatorService;
        this.forwarderOrderRepository = forwarderOrderRepository;
        this.carrierRepository = carrierRepository;
    }
    
    public List<CarrierInvoiceDTO> findAll(){
        return carrierInvoiceRepository.findAll().stream().map(carrierInvoiceMapper::mapToDTO).collect(Collectors.toList());
    }

    public CarrierInvoiceDTO findCarrierInvoiceById(Long id) {
        return carrierInvoiceRepository.findById(id).map(carrierInvoiceMapper::mapToDTO).orElseThrow(() -> new InvoiceNotFound("Invoice not found"));
    }
    
    @Transactional
    public CarrierInvoiceDTO addCarrierInvoice(CarrierInvoiceDTO carrierInvoiceDTO){
        CarrierInvoice invoice = carrierInvoiceMapper.mapToEntity(carrierInvoiceDTO);

        ForwardingOrder order = extractOrderFromInvoice(carrierInvoiceDTO);
        Carrier carrier = extractCarrierFromInvoice(carrierInvoiceDTO);

        changeInvoicingStatusForOrder(order);
        addAdditionalDataFroInvoice(invoice, order, carrier);

        CarrierInvoice saved = carrierInvoiceRepository.save(invoice);
        return carrierInvoiceMapper.mapToDTO(saved);
    }

    @Transactional
    public void markInvoiceAsPaid(Long invoiceId){
        CarrierInvoice invoice = carrierInvoiceRepository.findById(invoiceId).orElseThrow(() -> new InvoiceNotFound("Invoice not found"));
        if (invoice.getIsPaid()){
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

    private static void checkingUnauthorizedValueChange(CarrierInvoiceDTO currentDTO, CarrierInvoiceDTO updatedDTO) {
        if (currentDTO.getIsPaid() == true && updatedDTO.getIsPaid() == false){
            throw new CannotEditEntityException("Cannot change isPaid value from paid to false");
        }
    }

    private static void checkingPaidStatus(CarrierInvoice carrierInvoice) {
        if (carrierInvoice.getIsPaid()){
            throw new CannotEditEntityException("Cannot edit carrier invoice because is paid.");
        }
    }

    public List<CarrierInvoiceDTO> findAllCarrierInvoicesSortedBy(String sortBy) {
        return carrierInvoiceRepository.findAllCarrierInvoicesBy(sortBy).stream().map(carrierInvoiceMapper::mapToDTO).collect(Collectors.toList());
    }

    private static void changeCarrierBalance(CarrierInvoice invoice) {
        Carrier carrier = invoice.getCarrier();
        BigDecimal grossValue = invoice.getGrossValue();
        BigDecimal balance = carrier.getBalance();
        carrier.setBalance(balance.subtract(grossValue));
    }

    private void addAdditionalDataFroInvoice(CarrierInvoice invoice, ForwardingOrder order, Carrier carrier) {
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

    private static void changeInvoicingStatusForOrder(ForwardingOrder order) {
        order.setIsInvoiced(true);
    }

    private Carrier extractCarrierFromInvoice(CarrierInvoiceDTO carrierInvoiceDTO) {
        Long carrierId = carrierInvoiceDTO.getCarrierId();
        return carrierRepository.findById(carrierId).orElseThrow(() -> new CarrierNotFoundException("Carrier not found"));
    }

    private ForwardingOrder extractOrderFromInvoice(CarrierInvoiceDTO carrierInvoiceDTO) {
        Long orderId = carrierInvoiceDTO.getOrderId();
        return forwarderOrderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }


}
