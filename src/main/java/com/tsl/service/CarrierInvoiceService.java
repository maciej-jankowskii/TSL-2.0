package com.tsl.service;

import com.tsl.dtos.CarrierInvoiceDTO;
import com.tsl.exceptions.CarrierNotFoundException;
import com.tsl.exceptions.ForwardingOrderNotFoundException;
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
        return forwarderOrderRepository.findById(orderId).orElseThrow(() -> new ForwardingOrderNotFoundException("Order not found"));
    }


}
