package com.nikhil.ecommerce_backend.dto.general;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest
{
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
}
