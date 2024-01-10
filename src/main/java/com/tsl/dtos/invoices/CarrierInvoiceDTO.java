package com.tsl.dtos.invoices;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CarrierInvoiceDTO {
    private Long id;
    @NotBlank(message = "Invoice number cannot be null")
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private Boolean isPaid;
    private Long orderId;
    private Long carrierId;
}
