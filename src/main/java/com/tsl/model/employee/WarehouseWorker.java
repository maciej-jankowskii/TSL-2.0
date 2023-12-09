package com.tsl.model.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "warehouse_workers")
public class WarehouseWorker extends User{
    Boolean permissionsForklift;
    Boolean permissionsCrane;
}
