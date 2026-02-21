package com.nikhil.ecommerce_backend.dto.general;

import lombok.*;

@Data
@Builder
public class AccessTokenByRefreshToken
{
    private String newAccessToken;
    private long expiresIn;
    private String refreshToken;
}
