package com.nikhil.ecommerce_backend.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCustomerProfileRequest {

    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters")
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be exactly 10 digits")
    private String contact;
}