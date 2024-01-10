package com.tsl.repository.forwardingAndTransport;

import com.tsl.model.truck.Truck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TruckRepository extends CrudRepository<Truck, Long> {

    List<Truck> findAll();
    Optional<Truck> findById(Long id);

    @Query("SELECT tr FROM Truck tr ORDER BY " +
            "CASE WHEN :sortBy = 'brand' THEN tr.brand END ASC, " +
            "CASE WHEN :sortBy = 'technicalInspectionDate' THEN tr.technicalInspectionDate END ASC, " +
            "CASE WHEN :sortBy = 'type' THEN tr.type END ASC, " +
            "CASE WHEN :sortBy = 'insuranceDate' THEN tr.insuranceDate END ASC, " +
            "CASE WHEN :sortBy = 'assignedToDriver' THEN tr.assignedToDriver END ASC")
    List<Truck> findAllTrucksBy(@Param("sortBy") String sortBy);
}
