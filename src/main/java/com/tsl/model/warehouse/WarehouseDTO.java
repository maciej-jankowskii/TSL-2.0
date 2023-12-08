package com.tsl.model.warehouse;

import com.tsl.model.address.AddressDTO;
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
