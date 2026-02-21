package com.nikhil.ecommerce_backend.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RoleAuthority
{
    CUSTOMER(1),
    SELLER(2),
    ADMIN(3);
    private final int code;
}
