package com.nikhil.ecommerce_backend.dto.admin;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class AdminProductView {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private boolean isActive;
    private boolean isDeleted;
    private Category category;
    private List<Variation> variations;
    private Long sellerId;

    @Getter
    @Builder
    public static class Category {
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    public static class Variation {
        private Long id;
        private Double price;
        private Integer quantityAvailable;
        private boolean isActive;
        private String primaryImageUrl;
    }
}