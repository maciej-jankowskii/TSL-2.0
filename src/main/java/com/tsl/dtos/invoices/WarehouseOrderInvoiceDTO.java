package com.tsl.dtos.invoices;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class WarehouseOrderInvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private Boolean isPaid;
    private Long WarehouseOrderId;
}
