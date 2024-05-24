package com.tsl.mapper;

import com.tsl.dtos.warehouses.WarehouseOrderDTO;
import com.tsl.exceptions.*;
import com.tsl.model.contractor.Customer;
import com.tsl.model.warehouse.Warehouse;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.model.warehouse.order.WarehouseOrder;
import com.tsl.repository.forwardingAndTransport.CustomerRepository;
import com.tsl.repository.warehouses.GoodsRepository;
import com.tsl.repository.warehouses.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseOrderMapper {

    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final GoodsRepository goodsRepository;

    public WarehouseOrder mapToEntity(WarehouseOrderDTO warehouseOrderDTO){
        if (warehouseOrderDTO == null){
            throw new NullEntityException("Warehouse order data cannot be null");
        }
        WarehouseOrder order = new WarehouseOrder();
        order.setId(warehouseOrderDTO.getId());
        Warehouse warehouse = warehouseRepository.findById(warehouseOrderDTO.getWarehouseId()).orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
        order.setWarehouse(warehouse);
        Customer customer = customerRepository.findById(warehouseOrderDTO.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        order.setCustomer(customer);
        order.setDateOfReturn(warehouseOrderDTO.getDateOfReturn());

        List<Goods> goodsList = warehouseOrderDTO.getGoodsIds().stream()
                .map(goodsId -> goodsRepository.findById(goodsId)
                        .orElseThrow(() -> new GoodsNotFoundException("Goods not found with id: " + goodsId)))
                .collect(Collectors.toList());

        if (goodsList.isEmpty()) {
            throw new NoGoodsSelectedException("No goods selected");
        }

        order.setGoods(goodsList);
        order.setIsCompleted(warehouseOrderDTO.getIsCompleted());
        order.setDateAdded(warehouseOrderDTO.getDateAdded());
        order.setTotalCosts(warehouseOrderDTO.getTotalCosts());
        return order;
    }

    public WarehouseOrderDTO mapToDTO(WarehouseOrder warehouseOrder){
        if (warehouseOrder == null){
            throw new NullEntityException("Warehouse cannot be null");
        }
        WarehouseOrderDTO dto = new WarehouseOrderDTO();
        dto.setId(warehouseOrder.getId());
        dto.setWarehouseId(warehouseOrder.getWarehouse().getId());
        dto.setCustomerId(warehouseOrder.getCustomer().getId());
        dto.setDateOfReturn(warehouseOrder.getDateOfReturn());
        List<Long> goodsIds = warehouseOrder.getGoods().stream()
                .map(Goods::getId)
                .collect(Collectors.toList());

        dto.setGoodsIds(goodsIds);
        dto.setTotalCosts(warehouseOrder.getTotalCosts());
        dto.setDateAdded(warehouseOrder.getDateAdded());
        dto.setIsCompleted(warehouseOrder.getIsCompleted());
        return dto;
    }

}
