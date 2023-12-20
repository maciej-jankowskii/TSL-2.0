package com.tsl.service;

import com.tsl.dtos.WarehouseOrderDTO;
import com.tsl.enums.TypeOfGoods;
import com.tsl.exceptions.*;
import com.tsl.mapper.WarehouseOrderMapper;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.WarehouseOrderRepository;
import com.tsl.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseOrderService {

    private final WarehouseOrderRepository warehouseOrderRepository;
    private final WarehouseOrderMapper warehouseOrderMapper;
    private final WarehouseRepository warehouseRepository;
    private final StorageCostService storageCostService;

    public WarehouseOrderService(WarehouseOrderRepository warehouseOrderRepository,
                                 WarehouseOrderMapper warehouseOrderMapper, WarehouseRepository warehouseRepository,
                                 StorageCostService storageCostService) {
        this.warehouseOrderRepository = warehouseOrderRepository;
        this.warehouseOrderMapper = warehouseOrderMapper;
        this.warehouseRepository = warehouseRepository;
        this.storageCostService = storageCostService;
    }

    public List<WarehouseOrderDTO> findAllWarehouseOrders() {
        return warehouseOrderRepository.findAll().stream().map(warehouseOrderMapper::mapToDTO).collect(Collectors.toList());
    }

    public WarehouseOrderDTO findWarehouseOrder(Long id){
        return warehouseOrderRepository.findById(id).map(warehouseOrderMapper::mapToDTO).orElseThrow(() -> new WarehouseOrderNotFoundException("Warehouse order not found"));
    }

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
    public void markWarehouseOrderAsCompleted(Long warehouseOrderId){
        WarehouseOrder order = warehouseOrderRepository.findById(warehouseOrderId).orElseThrow(() -> new WarehouseOrderNotFoundException("Warehouse order not found"));
        if (order.getIsCompleted()){
            throw new WarehouseOrderIsAlreadyCompletedException("Order is already completed");
        }

        updateWarehouseAvailableArea(order);

        order.setIsCompleted(true);
        warehouseOrderRepository.save(order);
    }

    @Transactional
    public void updateWarehouseOrder(WarehouseOrderDTO warehouseOrderDTO){
        checkingTryingToChangeIsCompletedValue(warehouseOrderDTO);

        WarehouseOrder order = warehouseOrderMapper.mapToEntity(warehouseOrderDTO);

        checkingIsCompletedOrder(warehouseOrderDTO);
        warehouseOrderRepository.save(order);
    }

    private static void checkingIsCompletedOrder(WarehouseOrderDTO order) {
        if (order.getIsCompleted()){
            throw new CannotEditCompletedWarehouseOrder("Cannot edit completed order.");
        }
    }

    private static void checkingTryingToChangeIsCompletedValue(WarehouseOrderDTO warehouseOrderDTO){
        if (!warehouseOrderDTO.getIsCompleted()){
                throw new CannotEditCompletedWarehouseOrder("Cannot change isCompleted to false for an completed order.");}
    }

    public List<WarehouseOrderDTO> findAllWarehouseOrdersSortedBy(String sortBy){
        return warehouseOrderRepository.findAllOrderBy(sortBy).stream().map(warehouseOrderMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<WarehouseOrderDTO> findAllNotCompletedWarehouseOrders(){
        return warehouseOrderRepository.findByIsCompletedFalse().stream().map(warehouseOrderMapper::mapToDTO).collect(Collectors.toList());
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
        if (warehouse.getAvailableArea() < sum){
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