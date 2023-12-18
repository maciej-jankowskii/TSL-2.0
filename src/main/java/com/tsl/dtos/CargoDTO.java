package com.tsl.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CargoDTO {
    private Long id;
    @NotBlank(message = "Cargo number cannot be null")
    private String cargoNumber;
    @PositiveOrZero(message = "Price cannot negative number")
    private BigDecimal price;
    @NotBlank(message = "Currentcy cannot be null")
    private String currency;
    private LocalDate dateAdded;
    private LocalDate loadingDate;
    private LocalDate unloadingDate;
    @NotBlank(message = "Loading address cannot be null")
    private String loadingAddress;
    @NotBlank(message = "Loading address cannot be null")
    private String unloadingAddress;
    private String goods;
    private String description;
    private Boolean assignedToOrder;
    private Boolean invoiced;
    @Positive(message = "Customer ID cannot be 0")
    private Long customerId;
}
