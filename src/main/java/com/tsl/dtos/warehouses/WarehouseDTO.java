package com.tsl.dtos.warehouses;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDTO {
    private Long id;
    @NotBlank(message = "Type of goods cannot be null")
    private String typeOfGoods;
    @NotNull(message = "Address ID cannot be null")
    private Long addressId;
    private Boolean crane;
    private Boolean forklift;
    @NotNull(message = "Costs cannot be null")
    @Min(1)
    private Double costPer100SquareMeters;
    @NotNull(message = "Available area cannot be null")
    @Min(1)
    private Double availableArea;
}
