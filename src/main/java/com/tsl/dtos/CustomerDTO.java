package com.tsl.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CustomerDTO {
    private Long id;
    private String fullName;
    private String shortName;
    private String vatNumber;
    private String description;
    private Integer termOfPayment;
    private BigDecimal balance;
    private String paymentsRating;
}
