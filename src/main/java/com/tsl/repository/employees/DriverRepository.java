package com.tsl.repository.employees;

import com.tsl.model.employee.Driver;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends CrudRepository<Driver, Long> {
    List<Driver> findAll();
    Optional<Driver> findById(Long id);
    Boolean existsByEmail(String email);

    @Query("SELECT dr FROM Driver dr ORDER BY " +
            "CASE WHEN :sortBy = 'truck.id' THEN dr.truck.id END ASC," +
            "CASE WHEN :sortBy = 'licenceExpiryDate' THEN dr.licenceExpiryDate END ASC, " +
            "CASE WHEN :sortBy = 'workSystem' THEN dr.workSystem END ASC, " +
            "CASE WHEN :sortBy = 'assignedToTruck' THEN dr.assignedToTruck END ASC, " +
            "CASE WHEN :sortBy = 'firstName' THEN dr.firstName END ASC, " +
            "CASE WHEN :sortBy = 'lastName' THEN dr.lastName END ASC")
    List<Driver> findAllDriversBy(@Param("sortBy") String sortBy);
}
