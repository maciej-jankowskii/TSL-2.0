package com.tsl.service;

import com.tsl.dtos.GoodsDTO;
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

    public List<GoodsDTO> findAll(){
        return goodsRepository.findAll().stream().map(goodsMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<GoodsDTO> findAllNotAssignedToOrderGoods(){
        return goodsRepository.findAllByAssignedToOrderIsFalse().stream().map(goodsMapper::mapToDTO).collect(Collectors.toList());
    }

    public GoodsDTO findGoodsById(Long id){
        return goodsRepository.findById(id).map(goodsMapper::mapToDTO).orElseThrow(() -> new GoodsNotFoundException("Goods not found"));
    }

    @Transactional
    public GoodsDTO addGoods(GoodsDTO goodsDTO){
        Goods goods = goodsMapper.mapToEntity(goodsDTO);
        if (goodsRepository.existsByLabelAndAssignedToOrderFalse(goods.getLabel())){
            throw new NonUniqueLabelsException("Label must be unique among unassigned goods");
        }
        goods.setAssignedToOrder(false);
        Goods saved = goodsRepository.save(goods);
        return goodsMapper.mapToDTO(saved);
    }

    public List<GoodsDTO> findAllGoodsSortedBY(String sortBy){
        return goodsRepository.findAllOrderBy(sortBy).stream().map(goodsMapper::mapToDTO).collect(Collectors.toList());
    }
    @Transactional
    public void updateGoods(GoodsDTO goodsDTO){
        checkingTryingToChangeValueAssignedToOrder(goodsDTO);

        Goods goods = goodsMapper.mapToEntity(goodsDTO);

        checkingAssignedToOrder(goodsDTO);
        checkingLabelUnique(goodsDTO);

        goodsRepository.save(goods);
    }

    private static void checkingTryingToChangeValueAssignedToOrder(GoodsDTO goodsDTO) {
        if (!goodsDTO.getAssignedToOrder()) {
            throw new CannotEditGoodsAssignedToOrderException("Cannot change assignedToOrder to false for an item assigned to the order.");
        }
    }

    private void checkingLabelUnique(GoodsDTO goodsDTO) {
        if (goodsRepository.existsByLabelAndAssignedToOrderFalse(goodsDTO.getLabel())){
            throw new NonUniqueLabelsException("Label must be unique among unassigned goods");
        }
    }

    private static void checkingAssignedToOrder(GoodsDTO goodsDTO) {
        if (goodsDTO.getAssignedToOrder()){
            throw new CannotEditGoodsAssignedToOrderException("Cannot edit the item assigned to the order.");
        }
    }


}
