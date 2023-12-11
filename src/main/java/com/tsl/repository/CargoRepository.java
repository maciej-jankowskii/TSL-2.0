package com.tsl.repository;

import com.tsl.model.cargo.Cargo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CargoRepository extends CrudRepository<Cargo, Long> {
    Optional<Cargo> findById(Long id);
    List<Cargo> findAll();
}
