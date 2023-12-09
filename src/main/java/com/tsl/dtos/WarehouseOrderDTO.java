package com.tsl.dtos;

import com.tsl.dtos.CustomerDTO;
import com.tsl.dtos.WarehouseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class WarehouseOrderDTO {
    private Long id;
    private WarehouseDTO warehouse;
    private CustomerDTO customer;
    private LocalDate dateAdded;
    private LocalDate dateOfReturn;
}
