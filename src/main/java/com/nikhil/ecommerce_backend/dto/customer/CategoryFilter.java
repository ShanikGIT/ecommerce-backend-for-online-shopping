package com.nikhil.ecommerce_backend.dto.customer;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CategoryFilter {
    private List<String> brands;
    private PriceRange priceRange;

    @Getter
    @Builder
    public static class PriceRange {
        private Double minPrice;
        private Double maxPrice;
    }
}