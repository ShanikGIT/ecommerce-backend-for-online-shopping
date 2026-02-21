package com.nikhil.ecommerce_backend.dto.customer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerProfileResponse
{
    private Long id;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String contact;
    private String imageUrl;
}
