package com.tsl.model.warehouse.order;

import com.tsl.model.contractor.Customer;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.goods.Goods;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "warehouse_orders")
public class WarehouseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "warehouse_order_goods",
            joinColumns = @JoinColumn(name = "warehouse_order_id"),
            inverseJoinColumns = @JoinColumn(name = "goods_id"))
    private List<Goods> goods = new ArrayList<>();
    private LocalDate dateAdded;
    private LocalDate dateOfReturn;
    private Double totalCosts;
    private Boolean isCompleted;
}
