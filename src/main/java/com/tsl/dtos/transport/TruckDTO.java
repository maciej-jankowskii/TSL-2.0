package com.tsl.dtos.transport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TruckDTO {
    private Long id;
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @NotBlank
    private String type;
    private String plates;
    @NotNull
    private LocalDate technicalInspectionDate;
    @NotNull
    private LocalDate insuranceDate;
    private Boolean assignedToDriver;
    private Long transportPlannerId;
}
