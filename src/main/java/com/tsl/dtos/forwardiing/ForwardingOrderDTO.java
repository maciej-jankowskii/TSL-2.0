package com.tsl.dtos.forwardiing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ForwardingOrderDTO {
    private Long id;
    @NotBlank
    private String orderNumber;
    @NotNull
    private Long cargoId;
    @NotNull
    private BigDecimal price;
    @NotBlank
    private String currency;
    @NotNull
    private Long carrierId;
    private String typeOfTruck;
    private String truckNumbers;
    private BigDecimal margin;
    private Boolean isInvoiced;
    private Long forwarderId;
    private LocalDate dateAdded;
    private String orderStatus;
}
