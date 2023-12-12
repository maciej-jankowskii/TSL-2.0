package com.tsl.repository;

import com.tsl.model.employee.Forwarder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForwarderRepository extends CrudRepository<Forwarder, Long> {
    Optional<Forwarder> findById(Long id);
    Optional<Forwarder> findByEmail(String email);
}
