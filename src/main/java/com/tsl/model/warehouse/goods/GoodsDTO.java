package com.tsl.model.warehouse.goods;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsDTO {
    private String name;
    private String typeOfGoods;
    private Double quantity;
    private String label;
    private String description;
    private Double requiredArea;
    private Boolean assignedToOrder;
}
