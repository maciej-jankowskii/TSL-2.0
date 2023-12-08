package com.tsl.model.order;

import com.tsl.model.employee.TransportPlanner;
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
public class TransportOrder extends Order{
    @ManyToOne
    @JoinColumn(name = "transport_planner_id")
    private TransportPlanner transportPlanner;
    @ManyToOne
    @JoinColumn(name = "truck_id")
    private Truck truck;
}
