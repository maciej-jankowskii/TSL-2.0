package com.tsl.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ForwarderDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String telephone;
    private AddressDTO address;
    private BigDecimal basicGrossSalary;
    private LocalDate dateOfEmployment;
    private String formOfEmployment;
    private LocalDate contractExpiryDate;
    private Double extraPercentage;
    private BigDecimal totalMargin; // czy aby na pewno chcemy to pokazywac ?
    private Double salaryBonus;
}
