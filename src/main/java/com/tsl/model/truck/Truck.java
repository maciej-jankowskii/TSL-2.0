package com.tsl.model.truck;

import com.tsl.enums.TypeOfTruck;
import com.tsl.model.employee.Driver;
import com.tsl.model.employee.TransportPlanner;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "trucks")
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String model;
    @Enumerated(EnumType.STRING)
    private TypeOfTruck type;
    private String plates;
    private LocalDate technicalInspectionDate;
    private LocalDate insuranceDate;
    private Boolean assignedToDriver;
    @OneToMany(mappedBy = "truck")
    private List<Driver> drivers = new ArrayList<>();
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "transport_planner_id")
    private TransportPlanner transportPlanner;

}
