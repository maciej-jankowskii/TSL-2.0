package com.tsl.model.order;

import com.tsl.enums.TypeOfTruck;
import com.tsl.model.contractor.Carrier;
import com.tsl.model.employee.Forwarder;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "forwarding_orders")
public class ForwardingOrder extends Order{
    @ManyToOne
    @JoinColumn(name = "forwarder_id")
    private Forwarder forwarder;
    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;
    @Enumerated(EnumType.STRING)
    private TypeOfTruck typeOfTruck;
    private String truckNumbers;
    private BigDecimal margin;

}
