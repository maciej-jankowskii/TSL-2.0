package com.tsl.model.employee;

import com.tsl.model.order.ForwardingOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "forwarders")
public class Forwarder extends User{

    @OneToMany(mappedBy = "forwarder", fetch = FetchType.EAGER)
    private List<ForwardingOrder> forwardingOrders;
    private Double extraPercentage; //for example +20% above 3000 EUR per month
    private BigDecimal totalMargin;
    private Double salaryBonus;

}
