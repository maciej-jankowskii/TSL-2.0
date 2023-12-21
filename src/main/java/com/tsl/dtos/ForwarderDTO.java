package com.tsl.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ForwarderDTO {
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
    private Double extraPercentage;
    private BigDecimal totalMargin;
    private Double salaryBonus;
}
