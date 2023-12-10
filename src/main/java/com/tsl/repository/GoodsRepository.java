package com.tsl.repository;

import com.tsl.model.warehouse.goods.Goods;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsRepository extends CrudRepository<Goods, Long> {

    Optional<Goods> findById(Long id);
    List<Goods> findAll();
    List<Goods> findAllByAssignedToOrderIsFalse();
    Boolean existsByLabelAndAssignedToOrderFalse(String label);
    List<Goods> findAllByIdIn(List<Long> ids);
}
