package com.tsl.dtos.forwardiing;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CustomerWithBalanceDTO {
    private Long id;
    private String fullName;
    private String vatNumber;
    private BigDecimal balance;
}
