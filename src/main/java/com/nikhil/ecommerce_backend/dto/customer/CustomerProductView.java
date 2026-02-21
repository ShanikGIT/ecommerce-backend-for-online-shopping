package com.nikhil.ecommerce_backend.dto.customer;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CustomerProductView {
    private String name;
    private String description;
    private String brand;
    private Category category;
    private List<Variation> variations;

    @Getter
    @Builder
    public static class Category {
        private String name;
    }

    @Getter
    @Builder
    public static class Variation {
        private Double price;
        private Integer quantityAvailable;
        private String primaryImage;
        private Map<String, Object> metadata;
        private List<Image> secondaryImages;
    }

    @Getter
    @Builder
    public static class Image {
        private String imageUrl;
    }
}