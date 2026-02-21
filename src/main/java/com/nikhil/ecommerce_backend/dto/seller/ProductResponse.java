package com.nikhil.ecommerce_backend.dto.seller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private boolean isActive;
    private boolean isCancellable;
    private boolean isReturnable;
    private Category category;

    @Getter
    @Setter
    @Builder
    public static class Category {
        private Long id;
        private String name;
    }
}