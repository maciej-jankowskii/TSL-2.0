package com.tsl.model.employee;

import com.tsl.model.warehouse.Warehouse;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "warehouse_workers")
public class WarehouseWorker extends User{
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    private Boolean permissionsForklift;
    private Boolean permissionsCrane;
}
