package com.tsl.model.invoice;

import com.tsl.model.contractor.CarrierDTO;
import com.tsl.model.order.ForwardingOrderDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CarrierInvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private Boolean isPaid;
    private ForwardingOrderDTO orderDTO;
    private CarrierDTO carrierDTO; // pomyslec o samych Id lub nazwach
}
