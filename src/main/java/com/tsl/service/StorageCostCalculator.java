package com.tsl.service;

import com.tsl.model.warehouse.order.WarehouseOrder;

public interface StorageCostCalculator {

    Double calculateStorageCosts(WarehouseOrder warehouseOrder);
}
