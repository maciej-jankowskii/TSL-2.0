package com.tsl.repository;

import com.tsl.model.contractor.Carrier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends CrudRepository<Carrier, Long> {
    Optional<Carrier> findById(Long id);

    List<Carrier> findAll();

    @Query("SELECT c FROM Carrier c ORDER BY " +
            "CASE WHEN :sortBy = 'fullName' THEN c.fullName END ASC, " +
            "CASE WHEN :sortBy = 'shortName' THEN c.shortName END ASC, " +
            "CASE WHEN :sortBy = 'vatNumber' THEN c.vatNumber END ASC")
    List<Carrier> findAllCarriersBy(@Param("sortBy") String sortBy);
}
