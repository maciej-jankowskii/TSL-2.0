package com.tsl.model.cargo;

import com.tsl.model.contractor.CustomerDTO;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CargoDTO {
    private Long id;
    private String cargoNumber;
    private BigDecimal price;
    private String currency;
    private LocalDate dateAdded;
    private LocalDate loadingDate;
    private LocalDate unloadingDate;
    private String loadingAddress;
    private String unloadingAddress;
    private String goods;
    private String description;
    private Boolean assignedToOrder;
    private Boolean invoiced;
    private CustomerDTO customerDTO;  // mozna potem ewentualnie zmienic na Long customerId
}
