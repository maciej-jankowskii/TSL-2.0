package com.tsl.model.invoice;

import com.tsl.model.contractor.Carrier;
import com.tsl.model.order.ForwardingOrder;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "carrier_invoices")
public class CarrierInvoice extends Invoice{
    @OneToOne
    @JoinColumn(name = "order_id")
    private ForwardingOrder order;
    @ManyToOne
    @JoinColumn(name="carrier_id")
    private Carrier carrier;

}
