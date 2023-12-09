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
    private LocalDate dateAdded;
    private CargoDTO cargo;
    private BigDecimal price;
    private String currency;
    private String orderStatus;
    private Boolean isInvoiced;
    private ForwarderDTO forwarder;
    private CarrierDTO carrier;
    private String typeOfTruck;
    private String truckNumbers;
    private BigDecimal margin;
}
