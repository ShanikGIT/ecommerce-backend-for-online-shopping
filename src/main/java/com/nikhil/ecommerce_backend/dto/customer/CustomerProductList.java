package com.nikhil.ecommerce_backend.dto.customer;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CustomerProductList {
    private Long id;
    private String name;
    private String brand;
    private Category category;
    private List<CustomerProductView.Variation> variations;

    @Getter
    @Builder
    public static class Category {
        private String name;
    }
}