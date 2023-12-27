package com.tsl.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
public class TransportOrderDTO {

    private Long id;
    @NotBlank
    private String orderNumber;
    private LocalDate dateAdded;
    @NotNull
    private Long cargoId;
    @NotNull
    private BigDecimal price;
    @NotBlank
    private String currency;
    private String orderStatus;
    private Boolean isInvoiced;
    private Long transportPlannerId;
    private Long truckId;
}
