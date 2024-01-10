package com.tsl.mapper;

import com.tsl.dtos.warehouses.GoodsDTO;
import com.tsl.enums.TypeOfGoods;
import com.tsl.exceptions.NullEntityException;
import com.tsl.model.warehouse.goods.Goods;
import org.springframework.stereotype.Service;

@Service
public class GoodsMapper {

    public Goods mapToEntity(GoodsDTO dto) {
        if (dto == null) {
            throw new NullEntityException("Goods data cannot be null");
        }
        Goods goods = new Goods();
        goods.setId(dto.getId());
        goods.setTypeOfGoods(TypeOfGoods.valueOf(dto.getTypeOfGoods()));
        goods.setName(dto.getName());
        goods.setLabel(dto.getLabel());
        goods.setDescription(dto.getDescription());
        goods.setQuantity(dto.getQuantity());
        goods.setRequiredArea(dto.getRequiredArea());
        goods.setAssignedToOrder(dto.getAssignedToOrder());
        return goods;
    }

    public GoodsDTO mapToDTO(Goods goods) {
        if (goods == null) {
            throw new NullEntityException("Goods cannot be null");
        }
        GoodsDTO dto = new GoodsDTO();
        dto.setId(goods.getId());
        dto.setName(goods.getName());
        dto.setDescription(goods.getDescription());
        dto.setQuantity(goods.getQuantity());
        dto.setLabel(goods.getLabel());
        dto.setAssignedToOrder(goods.getAssignedToOrder());
        dto.setTypeOfGoods(String.valueOf(goods.getTypeOfGoods()));
        dto.setRequiredArea(goods.getRequiredArea());
        dto.setAssignedToOrder(goods.getAssignedToOrder());
        return dto;
    }
}
