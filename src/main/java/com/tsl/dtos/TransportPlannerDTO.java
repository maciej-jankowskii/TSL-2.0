package com.tsl.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TransportPlannerDTO {
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String telephone;
    @NotNull
    private Long addressId;
    private BigDecimal basicGrossSalary;
    @NotNull
    private LocalDate dateOfEmployment;
    @NotBlank
    private String formOfEmployment;
    private LocalDate contractExpiryDate;
    private Double salaryBonus;
    private List<@Positive(message = "Truck ID must be a positive number") Long> truckIds;
}
