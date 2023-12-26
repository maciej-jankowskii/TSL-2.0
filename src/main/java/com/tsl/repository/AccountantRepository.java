package com.tsl.repository;

import com.tsl.model.employee.Accountant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountantRepository extends CrudRepository<Accountant, Long> {

    Optional<Accountant> findById(Long id);
    List<Accountant> findAll();
    Boolean existsByEmail(String email);
}
