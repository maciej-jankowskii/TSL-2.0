package com.tsl.dtos;

import com.tsl.model.warehouse.Warehouse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class WarehouseWorkerDTO {
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String telephone;
    @NotNull
    private Long addressId;
    private BigDecimal basicGrossSalary;
    @NotNull
    private LocalDate dateOfEmployment;
    @NotBlank
    private String formOfEmployment;
    @NotNull
    private LocalDate contractExpiryDate;
    @NotNull
    private Long warehouseId;
    private Boolean permissionsForklift;
    private Boolean permissionsCrane;
}
