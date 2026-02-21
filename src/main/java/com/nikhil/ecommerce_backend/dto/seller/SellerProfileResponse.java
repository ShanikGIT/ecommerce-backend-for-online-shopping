package com.nikhil.ecommerce_backend.dto.seller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerProfileResponse
{
    private Long id;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String companyName;
    private String companyContact;
    private String imageUrl;
    private String gst;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}