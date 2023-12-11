package com.tsl.dtos;

import com.tsl.dtos.CustomerDTO;
import com.tsl.dtos.WarehouseDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class WarehouseOrderDTO {
    private Long id;
    @NotBlank(message = "Warehouse ID cannot be null")
    private Long warehouseId;
    @NotBlank(message = "Customer ID cannot be null")
    private Long customerId;
    @NotBlank(message = "Goods IDs cannot be null")
    private List<@Positive(message = "Goods ID must be a positive number") Long> goodsIds;
    private LocalDate dateAdded;
    @NotBlank(message = "Date of Return cannot be null")
    @Future(message = "Date of Return must be in the future")
    private LocalDate dateOfReturn;
    private Double totalCosts;
    private Boolean isCompleted;
}
