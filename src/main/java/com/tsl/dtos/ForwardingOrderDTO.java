package com.tsl.dtos;

import com.tsl.dtos.CargoDTO;
import com.tsl.dtos.CarrierDTO;
import com.tsl.dtos.ForwarderDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ForwardingOrderDTO {
    private Long id;
    private String orderNumber;
    private Long cargoId;
    private BigDecimal price;
    private String currency;
    private Long carrierId;
    private String typeOfTruck;
    private String truckNumbers;
    private BigDecimal margin;
}
