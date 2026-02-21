package com.nikhil.ecommerce_backend.exceptions;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}
