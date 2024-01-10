package com.tsl.dtos.forwardiing;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerDTO {
    private Long id;
    @NotBlank(message = "Full name cannot be null")
    private String fullName;
    private String shortName;
    private Long addressId;
    @Pattern(regexp = "^[A-Z]{2}\\d{8,}$", message = "Invalid VAT number")
    private String vatNumber;
    private String description;
    @NotNull(message = "Term of payment cannot be null")
    @Min(1)
    private Integer termOfPayment;
    @NotEmpty(message = "Contact person IDs cannot be null")
    private List<@Positive(message = "Goods ID must be a positive number") Long> contactPersonIds;
}
