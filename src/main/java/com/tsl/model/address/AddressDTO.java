package com.tsl.model.address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private Long id;
    private String country;
    private String postalCode;
    private String city;
    private String street;
    private String homeNo;
    private String flatNo;
}
