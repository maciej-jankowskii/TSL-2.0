package com.tsl.service.calculators;

import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.model.warehouse.order.WarehouseOrder;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StorageCostService implements StorageCostCalculator {

    @Override
    public Double calculateStorageCosts(WarehouseOrder warehouseOrder) {
        Warehouse warehouse = warehouseOrder.getWarehouse();
        Double costPer100SquareMeters = warehouse.getCostPer100SquareMeters();
        Long dayStored = ChronoUnit.DAYS.between(warehouseOrder.getDateAdded(), warehouseOrder.getDateOfReturn());

        Double requiredArea = getRequiredAreaOffAllGoodsInOrder(warehouseOrder);
        return calculateFinalCost(requiredArea, dayStored, costPer100SquareMeters);
    }

    private Double calculateFinalCost(Double requiredArea, Long dayStored, Double costPer100SquareMeters) {
        return (requiredArea / 100) * dayStored * costPer100SquareMeters;
    }

    private static Double getRequiredAreaOffAllGoodsInOrder(WarehouseOrder warehouseOrder) {
        List<Goods> goods = warehouseOrder.getGoods();
        return goods.stream()
                .mapToDouble(Goods::getRequiredArea)
                .sum();
    }
}
