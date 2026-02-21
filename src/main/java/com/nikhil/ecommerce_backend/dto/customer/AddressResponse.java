package com.nikhil.ecommerce_backend.dto.customer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse
{
    private Long id;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String label;
}
