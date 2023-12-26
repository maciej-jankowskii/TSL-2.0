package com.tsl.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
public class DetailedDriverDTO {
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
    @NotBlank
    private String driverLicenceNumber;
    @NotBlank
    private String licenceExpiryDate;
    private String workSystem;
    private Long truckId;
    private Boolean assignedToTruck;
    private Boolean mainDriver;
}
