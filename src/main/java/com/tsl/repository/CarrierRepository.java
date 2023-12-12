package com.tsl.repository;

import com.tsl.model.contractor.Carrier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends CrudRepository<Carrier, Long> {
    Optional<Carrier> findById(Long id);
    List<Carrier> findAll();
}
