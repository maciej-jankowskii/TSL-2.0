package com.tsl.dtos;

import com.tsl.dtos.CargoDTO;
import com.tsl.dtos.CustomerDTO;
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
