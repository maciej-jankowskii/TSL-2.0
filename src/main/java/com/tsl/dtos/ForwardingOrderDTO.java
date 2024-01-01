package com.tsl.dtos;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

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
    private Boolean isInvoiced;
    private Long forwarderId;
}
