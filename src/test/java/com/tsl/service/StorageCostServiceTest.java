package com.tsl.service;

import com.tsl.model.address.Address;
import com.tsl.model.contractor.Customer;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.service.calculators.StorageCostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StorageCostServiceTest {
    @InjectMocks
    private StorageCostService storageCostService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should calculate storage costs successfully")
    public void testCalculateStorageCosts_Success() {
        Warehouse warehouse = prepareWarehouse();
        List<Goods> goodsList = prepareGoodsList();
        WarehouseOrder order = prepareWarehouseOrder(warehouse, goodsList);


        Double result = storageCostService.calculateStorageCosts(order);

        assertEquals(150.0, result);
    }

    private static WarehouseOrder prepareWarehouseOrder(Warehouse warehouse, List<Goods> goodsList) {
        WarehouseOrder order = new WarehouseOrder();
        order.setId(1L);
        order.setCustomer(new Customer());
        order.setDateAdded(LocalDate.now().minusDays(5));
        order.setDateOfReturn(LocalDate.now());
        order.setGoods(goodsList);
        order.setWarehouse(warehouse);
        return order;
    }

    private List<Goods> prepareGoodsList() {
        Goods goods1 = new Goods();
        goods1.setRequiredArea(100.0);

        Goods goods2 = new Goods();
        goods2.setRequiredArea(200.0);

        return Arrays.asList(goods1, goods2);
    }

    private Warehouse prepareWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setForklift(true);
        warehouse.setCrane(true);
        warehouse.setAvailableArea(1000.0);
        warehouse.setAddress(new Address());
        warehouse.setCostPer100SquareMeters(10.0);
        return warehouse;
    }

}