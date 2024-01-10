package com.tsl.repository.warehouses;

import com.tsl.model.warehouse.goods.Goods;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT g FROM Goods g ORDER BY " +
            "CASE WHEN :sortBy = 'name' THEN g.name END ASC, " +
            "CASE WHEN :sortBy = 'typeOfGoods' THEN g.typeOfGoods END ASC, " +
            "CASE WHEN :sortBy = 'quantity' THEN g.quantity END ASC, " +
            "CASE WHEN :sortBy = 'label' THEN g.label END ASC, " +
            "CASE WHEN :sortBy = 'requiredArea' THEN g.requiredArea END ASC")
    List<Goods> findAllOrderBy(@Param("sortBy") String sortBy);
}
