package com.tsl.service.warehouses;

import com.tsl.dtos.warehouses.GoodsDTO;
import com.tsl.exceptions.*;
import com.tsl.mapper.GoodsMapper;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.repository.warehouses.GoodsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final GoodsMapper goodsMapper;

    public GoodsService(GoodsRepository goodsRepository, GoodsMapper goodsMapper) {
        this.goodsRepository = goodsRepository;
        this.goodsMapper = goodsMapper;
    }

    /**
     * Finding methods
     */

    public List<GoodsDTO> findAll() {
        return goodsRepository.findAll().stream().map(goodsMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<GoodsDTO> findAllNotAssignedToOrderGoods() {
        return goodsRepository.findAllByAssignedToOrderIsFalse().stream().map(goodsMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public GoodsDTO findGoodsById(Long id) {
        return goodsRepository.findById(id).map(goodsMapper::mapToDTO)
                .orElseThrow(() -> new GoodsNotFoundException("Goods not found"));
    }

    public List<GoodsDTO> findAllGoodsSortedBY(String sortBy) {
        return goodsRepository.findAllOrderBy(sortBy).stream().map(goodsMapper::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Create, update, delete methods
     */

    @Transactional
    public GoodsDTO addGoods(GoodsDTO goodsDTO) {
        Goods goods = goodsMapper.mapToEntity(goodsDTO);
        if (goodsRepository.existsByLabelAndAssignedToOrderFalse(goods.getLabel())) {
            throw new NonUniqueLabelsException("Label must be unique among unassigned goods");
        }
        goods.setAssignedToOrder(false);
        Goods saved = goodsRepository.save(goods);
        return goodsMapper.mapToDTO(saved);
    }

    @Transactional
    public void updateGoods(GoodsDTO current, GoodsDTO updated) {
        Goods goods = goodsMapper.mapToEntity(updated);
        checkingIsAssignedToOrder(goods);

        if (current.getAssignedToOrder() == true && updated.getAssignedToOrder() == false) {
            throw new CannotEditEntityException("Cannot change assignedToOrder value from true to false");
        }

        goodsRepository.save(goods);
    }

    public void deleteGoods(Long id) {
        Goods goods = goodsRepository.findById(id).orElseThrow(() -> new GoodsNotFoundException("Goods not found"));
        if (goods.getAssignedToOrder()) {
            throw new CannotDeleteEntityException("Cannot delete goods because goods are assigned to order");
        }
        goodsRepository.deleteById(id);
    }

    /**
     * Helper methods
     */

    private static void checkingIsAssignedToOrder(Goods goods) {
        if (goods.getAssignedToOrder()) {
            throw new CannotEditEntityException("Cannot edit goods assigned to the order.");
        }
    }

}
