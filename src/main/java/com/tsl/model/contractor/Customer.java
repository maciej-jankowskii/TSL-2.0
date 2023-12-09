package com.tsl.model.contractor;

import com.tsl.enums.PaymentRating;
import com.tsl.model.cargo.Cargo;
import com.tsl.model.invoice.CustomerInvoice;
import com.tsl.model.warehouse.order.WarehouseOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customers")
public class Customer extends Contractor {
    @OneToMany(mappedBy = "customer")
    private List<Cargo> cargos = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private PaymentRating paymentRating;
    @OneToMany(mappedBy = "customer")
    private List<WarehouseOrder> warehouseOrders = new ArrayList<>();
    @OneToMany(mappedBy = "customer")
    private List<CustomerInvoice> customerInvoices = new ArrayList<>();
}
