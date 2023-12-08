package com.tsl.model.warehouse;

import com.tsl.enums.TypeOfGoods;
import com.tsl.model.address.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeOfGoods typeOfGoods;
    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
    private Boolean crane;
    private Boolean forklift;
    private Double costPer100SquareMeters;
    private Double availableArea;
}
