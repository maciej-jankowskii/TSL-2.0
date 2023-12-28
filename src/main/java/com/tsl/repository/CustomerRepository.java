package com.tsl.repository;

import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findById(Long id);
    List<Customer> findAll();

    @Query("SELECT c FROM Customer c ORDER BY " +
            "CASE WHEN :sortBy = 'fullName' THEN c.fullName END ASC, " +
            "CASE WHEN :sortBy = 'shortName' THEN c.shortName END ASC, " +
            "CASE WHEN :sortBy = 'vatNumber' THEN c.vatNumber END ASC")
    List<Customer> findAllCustomersBy(@Param("sortBy") String sortBy);
}
