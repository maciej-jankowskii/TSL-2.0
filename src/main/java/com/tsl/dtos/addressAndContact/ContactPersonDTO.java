package com.tsl.dtos.addressAndContact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactPersonDTO {
    private Long id;
    @NotBlank(message = "First name cannot be null")
    private String firstName;
    @NotBlank(message = "Last name cannot be null")
    private String lastName;
    @NotBlank(message = "Email cannot be null")
    @Email(message = "Invalid e-mail address")
    private String email;
    @NotBlank(message = "Telephone number name cannot be null")
    private String telephone;
}
