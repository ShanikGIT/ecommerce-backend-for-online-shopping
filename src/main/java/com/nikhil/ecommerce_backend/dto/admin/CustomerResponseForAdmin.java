package com.nikhil.ecommerce_backend.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponseForAdmin {
    private Long id;
    private String fullName;
    private String email;
    private boolean isActive;
}
