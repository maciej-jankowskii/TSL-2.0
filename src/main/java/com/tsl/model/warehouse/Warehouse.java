package com.tsl.model.warehouse;

import com.tsl.enums.TypeOfGoods;
import com.tsl.model.address.Address;
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
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeOfGoods typeOfGoods;
    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
    private Boolean crane;
    private Boolean forklift;
    private Double costPer100SquareMeters;
    private Double availableArea;
    @OneToMany(mappedBy = "warehouse")
    private List<WarehouseOrder> warehouseOrders = new ArrayList<>();
}
