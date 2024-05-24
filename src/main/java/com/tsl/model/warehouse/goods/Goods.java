package com.tsl.model.warehouse.goods;

import com.tsl.enums.TypeOfGoods;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "goods")
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private TypeOfGoods typeOfGoods;
    private Double quantity;
    private String label;
    private String description;
    private Double requiredArea;
    private Boolean assignedToOrder;
}
