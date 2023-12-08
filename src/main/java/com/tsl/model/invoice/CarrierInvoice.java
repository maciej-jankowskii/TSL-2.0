package com.tsl.model.invoice;

import com.tsl.model.contractor.Carrier;
import com.tsl.model.order.ForwardingOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CarrierInvoice extends Invoice{
    @OneToOne
    @JoinColumn(name = "order_id")
    private ForwardingOrder order;
    @ManyToOne
    @JoinColumn(name="carrier_id")
    private Carrier carrier;

}
