package com.nikhil.ecommerce_backend.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PriceRange {
    private Double minPrice;
    private Double maxPrice;
}
