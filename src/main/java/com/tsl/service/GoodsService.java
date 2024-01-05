package com.tsl.service;

import com.tsl.dtos.GoodsDTO;
import com.tsl.exceptions.CannotDeleteEntityException;
import com.tsl.exceptions.CannotEditGoodsAssignedToOrderException;
import com.tsl.exceptions.GoodsNotFoundException;
import com.tsl.exceptions.NonUniqueLabelsException;
import com.tsl.mapper.GoodsMapper;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.repository.GoodsRepository;
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
     Finding methods
     */

    public List<GoodsDTO> findAll() {
        return goodsRepository.findAll().stream().map(goodsMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<GoodsDTO> findAllNotAssignedToOrderGoods() {
        return goodsRepository.findAllByAssignedToOrderIsFalse().stream().map(goodsMapper::mapToDTO).collect(Collectors.toList());
    }

    public GoodsDTO findGoodsById(Long id) {
        return goodsRepository.findById(id).map(goodsMapper::mapToDTO).orElseThrow(() -> new GoodsNotFoundException("Goods not found"));
    }

    public List<GoodsDTO> findAllGoodsSortedBY(String sortBy) {
        return goodsRepository.findAllOrderBy(sortBy).stream().map(goodsMapper::mapToDTO).collect(Collectors.toList());
    }

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

    /**
     Create, update, delete methods
     */

    @Transactional
    public void updateGoods(GoodsDTO current, GoodsDTO updated) {
        Goods goods = goodsMapper.mapToEntity(updated);
        checkingIsAssignedToOrder(goods);

        if (current.getAssignedToOrder() == true && updated.getAssignedToOrder() == false) {
            throw new CannotEditGoodsAssignedToOrderException("Cannot change assignedToOrder value from true to false");
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
            throw new CannotEditGoodsAssignedToOrderException("Cannot edit goods assigned to the order.");
        }
    }

}
