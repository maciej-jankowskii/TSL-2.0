package com.tsl.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactPersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
}
