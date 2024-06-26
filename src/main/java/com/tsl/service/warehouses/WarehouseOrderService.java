package com.tsl.service.warehouses;

import com.tsl.dtos.warehouses.WarehouseOrderDTO;
import com.tsl.enums.TypeOfGoods;
import com.tsl.exceptions.*;
import com.tsl.mapper.WarehouseOrderMapper;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.warehouses.WarehouseOrderRepository;
import com.tsl.repository.warehouses.WarehouseRepository;
import com.tsl.service.calculators.StorageCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseOrderService {

    private final WarehouseOrderRepository warehouseOrderRepository;
    private final WarehouseOrderMapper warehouseOrderMapper;
    private final WarehouseRepository warehouseRepository;
    private final StorageCostService storageCostService;


    /**
     * Finding methods
     */

    public List<WarehouseOrderDTO> findAllWarehouseOrders() {
        return warehouseOrderRepository.findAll().stream().map(warehouseOrderMapper::mapToDTO).collect(Collectors.toList());
    }

    public WarehouseOrderDTO findWarehouseOrder(Long id) {
        return warehouseOrderRepository.findById(id).map(warehouseOrderMapper::mapToDTO)
                .orElseThrow(() -> new OrderNotFoundException("Warehouse order not found"));
    }

    public List<WarehouseOrderDTO> findAllWarehouseOrdersSortedBy(String sortBy) {
        return warehouseOrderRepository.findAllOrderBy(sortBy).stream().map(warehouseOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<WarehouseOrderDTO> findAllNotCompletedWarehouseOrders() {
        return warehouseOrderRepository.findByIsCompletedFalse().stream().map(warehouseOrderMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create, update, delete methods
     */

    @Transactional
    public WarehouseOrderDTO addWarehouseOrder(WarehouseOrderDTO warehouseOrderDTO) {
        WarehouseOrder warehouseOrder = warehouseOrderMapper.mapToEntity(warehouseOrderDTO);
        List<Goods> goodsList = warehouseOrder.getGoods();
        Warehouse warehouse = warehouseOrder.getWarehouse();

        TypeOfGoods commonGoodsType = typeCompatibilityChecking(goodsList);
        Double requiredAreaSum = calculationRequiredArea(goodsList);
        checkingCompatibilityOfGoodsAndWarehouseTypes(warehouse, commonGoodsType);
        checkingRequiredArea(warehouse, requiredAreaSum);
        changeAssignedToOrderOnGoodsEntity(goodsList);
        addAdditionalDataForOrder(warehouseOrder);

        warehouseRepository.save(warehouse);
        WarehouseOrder saved = warehouseOrderRepository.save(warehouseOrder);
        return warehouseOrderMapper.mapToDTO(saved);
    }

    @Transactional
    public void markWarehouseOrderAsCompleted(Long warehouseOrderId) {
        WarehouseOrder order = warehouseOrderRepository.findById(warehouseOrderId)
                .orElseThrow(() -> new OrderNotFoundException("Warehouse order not found"));
        if (order.getIsCompleted()) {
            throw new WarehouseOrderIsAlreadyCompletedException("Order is already completed");
        }

        updateWarehouseAvailableArea(order);

        order.setIsCompleted(true);
        warehouseOrderRepository.save(order);
    }

    @Transactional
    public void updateWarehouseOrder(WarehouseOrderDTO currentDTO, WarehouseOrderDTO updatedDTO) {
        WarehouseOrder order = warehouseOrderMapper.mapToEntity(updatedDTO);
        checkingIsCompletedOrder(order);

        if (currentDTO.getIsCompleted() == true && updatedDTO.getIsCompleted() == false) {
            throw new CannotEditEntityException("Cannot change isCompleted value from true to false");
        }
        warehouseOrderRepository.save(order);
    }

    /**
     * Helper methods
     */

    private static void checkingIsCompletedOrder(WarehouseOrder order) {
        if (order.getIsCompleted()) {
            throw new CannotEditEntityException("Cannot edit completed order.");
        }
    }


    private static void updateWarehouseAvailableArea(WarehouseOrder order) {
        Warehouse warehouse = order.getWarehouse();
        double requiredArea = order.getGoods().stream().mapToDouble(Goods::getRequiredArea).sum();
        warehouse.setAvailableArea(warehouse.getAvailableArea() + requiredArea);
    }

    private static void changeAssignedToOrderOnGoodsEntity(List<Goods> goodsList) {
        goodsList.forEach(goods -> {
            goods.setAssignedToOrder(true);
        });
    }

    private static void checkingRequiredArea(Warehouse warehouse, Double sum) {
        if (warehouse.getAvailableArea() < sum) {
            throw new InsufficientWarehouseSpaceException("Not enough space in the warehouse");
        } else {
            warehouse.setAvailableArea(warehouse.getAvailableArea() - sum);
        }
    }

    private static void checkingCompatibilityOfGoodsAndWarehouseTypes(Warehouse warehouse, TypeOfGoods commonGoodsType) {
        if (commonGoodsType != warehouse.getTypeOfGoods()) {
            throw new IncompatibleGoodsTypeException("The type of goods is not compatible with the warehouse type");
        }
    }

    private void addAdditionalDataForOrder(WarehouseOrder warehouseOrder) {
        warehouseOrder.setTotalCosts(0.0);
        warehouseOrder.setIsCompleted(false);
        warehouseOrder.setDateAdded(LocalDate.now());
        Double storageCosts = storageCostService.calculateStorageCosts(warehouseOrder);
        warehouseOrder.setTotalCosts(storageCosts);
    }

    private TypeOfGoods typeCompatibilityChecking(List<Goods> goodsList) {
        TypeOfGoods commonGoodsType = goodsList.get(0).getTypeOfGoods();
        if (!goodsList.stream().allMatch(goods -> goods.getTypeOfGoods() == commonGoodsType)) {
            throw new IncompatibleGoodsTypeException("Not all goods have the same type");
        }
        return commonGoodsType;
    }

    private static Double calculationRequiredArea(List<Goods> selectedGoods) {
        return selectedGoods.stream()
                .mapToDouble(Goods::getRequiredArea).sum();
    }
}