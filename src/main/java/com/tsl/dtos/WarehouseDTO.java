package com.tsl.dtos;

import com.tsl.dtos.AddressDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDTO {
    private Long id;
    private String typeOfGoods;
    private AddressDTO address;
    private Boolean crane;
    private Boolean forklift;
    private Double costPer100SquareMeters;
    private Double availableArea;
}
