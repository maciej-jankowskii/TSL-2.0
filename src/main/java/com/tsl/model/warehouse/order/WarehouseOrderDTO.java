package com.tsl.model.warehouse.order;

import com.tsl.model.contractor.CustomerDTO;
import com.tsl.model.warehouse.WarehouseDTO;
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
