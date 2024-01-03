package com.tsl.repository;

import com.tsl.model.cargo.Cargo;
import com.tsl.model.order.TransportOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CargoRepository extends CrudRepository<Cargo, Long> {
    Optional<Cargo> findById(Long id);

    List<Cargo> findAll();

    @Query("SELECT c FROM Cargo c ORDER BY " +
            "CASE WHEN :sortBy = 'cargoNumber' THEN c.cargoNumber END ASC, " +
            "CASE WHEN :sortBy = 'price' THEN c.price END ASC, " +
            "CASE WHEN :sortBy = 'dateAdded' THEN c.dateAdded END ASC, " +
            "CASE WHEN :sortBy = 'loadingDate' THEN c.loadingDate END ASC, " +
            "CASE WHEN :sortBy = 'unloadingDate' THEN c.unloadingDate END ASC, " +
            "CASE WHEN :sortBy = 'assignedToOrder' THEN c.assignedToOrder END ASC, " +
            "CASE WHEN :sortBy = 'invoiced' THEN c.invoiced END ASC ")
    List<Cargo> findAllCargosBy(@Param("sortBy") String sortBy);

    List<Cargo> findAllByAssignedToOrderIsFalse();

    List<Cargo> findAllByInvoicedFalse();

}
