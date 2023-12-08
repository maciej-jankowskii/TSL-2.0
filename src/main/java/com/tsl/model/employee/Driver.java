package com.tsl.model.employee;

import com.tsl.model.truck.Truck;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Driver extends User{
    private String driverLicenceNumber;
    private String licenceExpiryDate;
    private String workSystem;
    @ManyToOne
    @JoinColumn(name = "truck_id")
    private Truck truck;
    private Boolean assignedToTruck;
    private Boolean mainDriver;
}
