package com.tsl.dtos;

import com.tsl.dtos.CustomerDTO;
import com.tsl.dtos.WarehouseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter

public class WarehouseOrderDTO {
    private Long id;
    private Long warehouseId;
    private Long customerId;
    private List<Long> goodsIds;
    private LocalDate dateAdded;
    private LocalDate dateOfReturn;
    private Double totalCosts;
}
