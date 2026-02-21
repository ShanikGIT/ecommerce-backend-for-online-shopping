package com.nikhil.ecommerce_backend.constants;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductRating {

    ONE(1, "Very Poor"),
    TWO(2, "Poor"),
    THREE(3, "Average"),
    FOUR(4, "Good"),
    FIVE(5, "Excellent");

    private final int value;
    private final String description;
}

