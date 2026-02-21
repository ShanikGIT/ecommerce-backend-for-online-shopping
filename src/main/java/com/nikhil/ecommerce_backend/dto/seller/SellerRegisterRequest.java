package com.nikhil.ecommerce_backend.dto.seller;
import com.nikhil.ecommerce_backend.entities.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;

@Data
public class SellerRegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email domain"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one uppercase, one lowercase, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Invalid GST number format"
    )
    private String gst;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Contact number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Contact number must be 10 digits and start with 6–9")
    private String companyContact;

    @Valid
    @NotNull(message = "Address is mandatory")
    private UpdateAddressRequest address;


}
