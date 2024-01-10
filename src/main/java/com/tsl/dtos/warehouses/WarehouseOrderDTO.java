package com.tsl.dtos.warehouses;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class WarehouseOrderDTO {
    private Long id;
    @NotNull(message = "Warehouse ID cannot be null")
    private Long warehouseId;
    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;
    @NotEmpty(message = "Goods IDs cannot be null")
    private List<@Positive(message = "Goods ID must be a positive number") Long> goodsIds;
    private LocalDate dateAdded;
    @Future(message = "Date of Return must be in the future")
    private LocalDate dateOfReturn;
    private Double totalCosts;
    private Boolean isCompleted;
}
