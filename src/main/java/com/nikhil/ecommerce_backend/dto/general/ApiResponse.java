package com.nikhil.ecommerce_backend.dto.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
}