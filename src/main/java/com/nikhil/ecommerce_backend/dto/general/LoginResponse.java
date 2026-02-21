package com.nikhil.ecommerce_backend.dto.general;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String message;
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    private String email;
    private List<String> roles;
}