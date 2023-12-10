package com.tsl.service;

import com.tsl.dtos.GoodsDTO;
import com.tsl.exceptions.NonUniqueLabelsException;
import com.tsl.mapper.GoodsMapper;
import com.tsl.model.warehouse.goods.Goods;
import com.tsl.repository.GoodsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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

    @Transactional
    public GoodsDTO addGoods(GoodsDTO goodsDTO){
        Goods goods = goodsMapper.mapToEntity(goodsDTO);
        if (goodsRepository.existsByLabelAndAssignedToOrderFalse(goods.getLabel())){
            throw new NonUniqueLabelsException("Label must be unique among unassigned goods");
        }
        Goods saved = goodsRepository.save(goods);
        return goodsMapper.mapToDTO(saved);
    }


}
