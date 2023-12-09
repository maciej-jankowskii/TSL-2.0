package com.tsl.model.invoice;

import com.tsl.model.warehouse.order.WarehouseOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "warehouse_order_invoices")
public class WarehouseOrderInvoice extends Invoice{
    @OneToOne
    @JoinColumn(name = "warehouse_order_id")
    private WarehouseOrder warehouseOrder;
}
