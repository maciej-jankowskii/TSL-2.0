package com.tsl.model.invoice;

import com.tsl.model.cargo.CargoDTO;
import com.tsl.model.contractor.CustomerDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CustomerInvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private Boolean isPaid;
    private CargoDTO cargoDTO;
    private CustomerDTO customerDTO;
}
