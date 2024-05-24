package com.tsl.model.order;

import com.tsl.enums.Currency;
import com.tsl.enums.OrderStatus;
import com.tsl.model.cargo.Cargo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "company_orders")
@Inheritance(strategy = InheritanceType.JOINED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private LocalDate dateAdded;
    @OneToOne
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private Boolean isInvoiced;
}
