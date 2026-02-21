package com.nikhil.ecommerce_backend.dto.seller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSellerProfileRequest {
    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters")
    private String lastName;

    private String companyName;

    private String companyContact;
}