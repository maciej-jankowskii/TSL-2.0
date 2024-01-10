package com.tsl.service.calculators;

import com.tsl.model.warehouse.order.WarehouseOrder;

public interface StorageCostCalculator {
    Double calculateStorageCosts(WarehouseOrder warehouseOrder);
}
