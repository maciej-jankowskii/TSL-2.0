package com.tsl.model.cargo;

import com.tsl.enums.Currency;
import com.tsl.model.contractor.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cargos")
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cargoNumber;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private LocalDate dateAdded;
    private LocalDate loadingDate;
    private LocalDate unloadingDate;
    private String loadingAddress;
    private String unloadingAddress;
    private String goods;
    private String description;
    private Boolean assignedToOrder;
    private Boolean invoiced;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
