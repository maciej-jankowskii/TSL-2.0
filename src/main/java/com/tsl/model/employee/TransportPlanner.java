package com.tsl.model.employee;

import com.tsl.model.order.TransportOrder;
import com.tsl.model.truck.Truck;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TransportPlanner extends User{
    @OneToMany(mappedBy = "transportPlanner")
    private List<Truck> companyTrucks;
    @OneToMany(mappedBy = "transportPlanner")
    private List<TransportOrder> transportOrders;
    private Double salaryBonus;  // + for example 600PLN per truck
}
