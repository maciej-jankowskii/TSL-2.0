package com.tsl.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private Long id;
    @NotBlank(message = "County cannot be null")
    private String country;
    @NotBlank(message = "Postal code cannot be null")
    private String postalCode;
    @NotBlank(message = "City cannot be null")
    private String city;
    @NotBlank(message = "Street cannot be null")
    private String street;
    @NotBlank(message = "Home No cannot be null")
    private String homeNo;
    private String flatNo;
}
