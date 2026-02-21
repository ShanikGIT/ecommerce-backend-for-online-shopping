package com.nikhil.ecommerce_backend.dto.customer;
import com.nikhil.ecommerce_backend.constants.AddressLabel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddAddressRequest {
    @NotBlank
    private String addressLine;
    @NotBlank
    private String city;
    @NotBlank
    private String state;
    @NotBlank
    private String country;
    @NotBlank
    private String zipCode;
    @NotNull
    @Enumerated(EnumType.STRING)
    private AddressLabel label;
}