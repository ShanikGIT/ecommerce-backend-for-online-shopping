package com.nikhil.ecommerce_backend.dto.seller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@Builder
public class ProductVariationResponse {
    private Long id;
    private Integer quantityAvailable;
    private Double price;
    private boolean isActive;
    private String primaryImageUrl;
    private Map<String, Object> metadata;
    private ParentProduct product;

    @Getter
    @Setter
    @Builder
    public static class ParentProduct {
        private Long id;
        private String name;
        private String brand;
    }
}