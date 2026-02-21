package com.nikhil.ecommerce_backend.dto.seller;

import com.nikhil.ecommerce_backend.constants.AddressLabel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateAddressRequest
{
    private String addressLine;
    private String city;
    private String state;
    private String country;
    @NotBlank(message = "ZIP code is required")
    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "ZIP code must be a valid 6-digit number and starts with 1-9")
    private String zipCode;
    @Enumerated(EnumType.STRING)
    private AddressLabel label;

}