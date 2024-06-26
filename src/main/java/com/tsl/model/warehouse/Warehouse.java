package com.tsl.model.warehouse;

import com.tsl.enums.TypeOfGoods;
import com.tsl.model.address.Address;
import com.tsl.model.employee.WarehouseWorker;
import com.tsl.model.warehouse.order.WarehouseOrder;
import jakarta.persistence.*;
import lombok.*;

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
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.EAGER)
    private List<WarehouseOrder> warehouseOrders = new ArrayList<>();
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.EAGER)
    private List<WarehouseWorker> warehouseWorkers = new ArrayList<>();
}
