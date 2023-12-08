package com.tsl.model.employee;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WarehouseWorker extends User{
    Boolean permissionsForklift;
    Boolean permissionsCrane;
}
