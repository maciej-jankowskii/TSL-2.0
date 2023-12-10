package com.tsl.service;

import com.tsl.dtos.WarehouseOrderDTO;
import com.tsl.enums.TypeOfGoods;
import com.tsl.exceptions.IncompatibleGoodsTypeException;
import com.tsl.exceptions.InsufficientWarehouseSpaceException;
import com.tsl.exceptions.NoGoodsSelectedException;
import com.tsl.mapper.WarehouseOrderMapper;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.CustomerRepository;
import com.tsl.repository.GoodsRepository;
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
    private final CustomerRepository customerRepository;
    private final GoodsRepository goodsRepository;
    private final StorageCostService storageCostService;

    public WarehouseOrderService(WarehouseOrderRepository warehouseOrderRepository, WarehouseOrderMapper warehouseOrderMapper, WarehouseRepository warehouseRepository, CustomerRepository customerRepository, GoodsRepository goodsRepository, StorageCostService storageCostService) {
        this.warehouseOrderRepository = warehouseOrderRepository;
        this.warehouseOrderMapper = warehouseOrderMapper;
        this.warehouseRepository = warehouseRepository;
        this.customerRepository = customerRepository;
        this.goodsRepository = goodsRepository;
        this.storageCostService = storageCostService;
    }

    public List<WarehouseOrderDTO> findAllWarehouseOrders() {
        return warehouseOrderRepository.findAll().stream().map(warehouseOrderMapper::mapToDTO).collect(Collectors.toList());
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