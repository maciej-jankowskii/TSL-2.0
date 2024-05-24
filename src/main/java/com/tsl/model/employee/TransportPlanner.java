package com.tsl.model.employee;

import com.tsl.model.order.TransportOrder;
import com.tsl.model.truck.Truck;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transport_planners")
public class TransportPlanner extends User{
    @OneToMany(mappedBy = "transportPlanner", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Truck> companyTrucks = new ArrayList<>();
    @OneToMany(mappedBy = "transportPlanner", fetch = FetchType.EAGER)
    private List<TransportOrder> transportOrders;
    private Double salaryBonus;


}
